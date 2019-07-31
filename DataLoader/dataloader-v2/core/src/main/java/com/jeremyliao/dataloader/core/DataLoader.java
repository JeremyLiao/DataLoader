package com.jeremyliao.dataloader.core;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyliao.dataloader.base.common.LoaderInfo;
import com.jeremyliao.dataloader.base.utils.EncryptUtils;
import com.jeremyliao.dataloader.core.common.Const;
import com.jeremyliao.dataloader.core.helper.LoaderInfoHelper;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader;
import com.jeremyliao.dataloader.core.source.DataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaohailiang on 2019-07-25.
 */
public class DataLoader {

    private static final String TAG = "DataLoader";
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();

    private static Map<String, Map<String, Map<Integer, List<LoaderInfo>>>> loaderInfoMap = new HashMap<>();
    private static Map<String, Object> loaderInfoCache = new HashMap<>();
    private static Map<Class, Object> proxyCache = new HashMap<>();
    private static ExecutorService executor;

    /**
     * 初始化
     */
    public static void init() {
        doInit();
    }

    /**
     * doInit
     */
    private static void doInit() {
        try {
            Gson gson = new Gson();
            String json = LoaderInfoHelper.getLoaderInfoJson();
            Type type = new TypeToken<List<LoaderInfo>>() {
            }.getType();
            List<LoaderInfo> loaderInfos = gson.fromJson(json, type);
            if (loaderInfos != null && loaderInfos.size() > 0) {
                for (LoaderInfo loaderInfo : loaderInfos) {
                    if (!loaderInfoMap.containsKey(loaderInfo.targetClass)) {
                        loaderInfoMap.put(loaderInfo.targetClass, new HashMap<String, Map<Integer, List<LoaderInfo>>>());
                    }
                    Map<String, Map<Integer, List<LoaderInfo>>> subMap = loaderInfoMap.get(loaderInfo.targetClass);
                    if (!subMap.containsKey(loaderInfo.method)) {
                        subMap.put(loaderInfo.method, new HashMap<Integer, List<LoaderInfo>>());
                    }
                    Map<Integer, List<LoaderInfo>> subSubMap = subMap.get(loaderInfo.method);
                    int paramSize = loaderInfo.paramTypes == null ? 0 : loaderInfo.paramTypes.length;
                    if (!subSubMap.containsKey(paramSize)) {
                        subSubMap.put(paramSize, new ArrayList<LoaderInfo>());
                    }
                    subSubMap.get(paramSize).add(loaderInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取代理
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T get(Class<T> type) {
        if (proxyCache.containsKey(type)) {
            return (T) proxyCache.get(type);
        }
        Object proxyInstance = Proxy.newProxyInstance(DataLoader.class.getClassLoader(),
                new Class[]{type},
                new InterfaceHandler(type));
        proxyCache.put(type, proxyInstance);
        return (T) proxyInstance;
    }

    private static ExecutorService getExecutor() {
        if (executor != null) {
            return executor;
        }
        return DEFAULT_EXECUTOR;
    }

    public static void setExecutor(ExecutorService executor) {
        DataLoader.executor = executor;
    }

    /**
     * 动态代理handler
     */
    private static class InterfaceHandler implements InvocationHandler {

        private final Class interfaceType;

        InterfaceHandler(Class interfaceType) {
            this.interfaceType = interfaceType;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            DefaultDataSource dataSource = new DefaultDataSource();
            try {
                LoaderInfo loaderInfo = getLoaderInfo(method);
                if (!loaderInfoCache.containsKey(loaderInfo.loaderClass)) {
                    Class loaderClass = Class.forName(loaderInfo.loaderClass);
                    Object loaderInstance = loaderClass.newInstance();
                    loaderInfoCache.put(loaderInfo.loaderClass, loaderInstance);
                }
                Object loaderInstance = loaderInfoCache.get(loaderInfo.loaderClass);
                if (loaderInstance instanceof CallableDataLoader) {
                    DataLoadTask task = new DataLoadTask(dataSource, loaderInstance, args);
                    getExecutor().execute(task);
                } else if (loaderInstance instanceof LiveDataLoader) {
                    processLoad(dataSource, loaderInstance, args);
                }
                return dataSource;
            } catch (Exception e) {
                dataSource.error.postValue(e);
            }
            return dataSource;
        }

        private LoaderInfo getLoaderInfo(Method method) {
            Log.d(TAG, "method.getParameterTypes().length: " + method.getParameterTypes().length);
            List<LoaderInfo> loaderInfos = loaderInfoMap
                    .get(interfaceType.getCanonicalName())
                    .get(method.getName())
                    .get(method.getParameterTypes().length);
            if (loaderInfos.size() == 1) {
                return loaderInfos.get(0);
            }
            for (LoaderInfo loaderInfo : loaderInfos) {
                if (match(loaderInfo, method)) {
                    return loaderInfo;
                }
            }
            return null;
        }

        private boolean match(LoaderInfo loaderInfo, Method method) {
            if (!TextUtils.equals(loaderInfo.loaderClass, interfaceType.getCanonicalName())) {
                return false;
            }
            if (!TextUtils.equals(loaderInfo.method, method.getName())) {
                return false;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            int paramSize1 = loaderInfo.paramTypes != null ? loaderInfo.paramTypes.length : 0;
            int paramSize2 = parameterTypes.length;
            if (paramSize1 != paramSize2) {
                return false;
            }
            if (paramSize1 > 0) {
                for (int i = 0; i < paramSize1; i++) {
                    String paramType = loaderInfo.paramTypes[i];
                    String name = parameterTypes[i].getCanonicalName();
                    if (!paramType.equals(name)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private String getKey(Method method) {
            StringBuilder sb = new StringBuilder();
            sb.append(interfaceType.getCanonicalName());
            sb.append(method.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 0) {
                for (Class<?> paramType : parameterTypes) {
                    sb.append(paramType.getCanonicalName());
                }
            }
            String feature = sb.toString();
            Log.d(TAG, "feature: " + feature);
            return EncryptUtils.encryptMD5ToString(feature);
        }

        /**
         * 对于LiveDataLoader，用反射的方式调用实际的loader
         *
         * @param dataSource
         * @param loaderInstance
         * @param args
         */
        private void processLoad(DefaultDataSource dataSource, Object loaderInstance, Object[] args) {
            Method[] methods = loaderInstance.getClass().getMethods();
            if (methods.length > 0) {
                for (Method method : methods) {
                    if (method.getName().equals("load")) {
                        try {
                            int paramSize = args != null ? args.length + 1 : 1;
                            Object[] params = new Object[paramSize];
                            params[0] = dataSource.result;
                            if (paramSize > 1) {
                                for (int i = 1; i < paramSize; i++) {
                                    params[i] = args[i - 1];
                                }
                            }
                            method.invoke(loaderInstance, params);
                        } catch (Exception e) {
                            if (e instanceof InvocationTargetException) {
                                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                                if (targetException != null) {
                                    dataSource.error.postValue(targetException);
                                    return;
                                }
                            }
                            dataSource.error.postValue(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * DataLoadTask，对应CallableDataLoader的方式
     *
     * @param <T>
     */
    private static class DataLoadTask<T> implements Runnable {

        final DefaultDataSource<T> dataSource;
        final Object loaderInstance;
        final Object[] args;

        DataLoadTask(DefaultDataSource<T> dataSource, Object loaderInstance, Object[] args) {
            this.dataSource = dataSource;
            this.loaderInstance = loaderInstance;
            this.args = args;
        }

        @Override
        public void run() {
            if (loaderInstance instanceof CallableDataLoader) {
                Method[] methods = loaderInstance.getClass().getMethods();
                if (methods.length > 0) {
                    for (Method method : methods) {
                        if (method.getName().equals("call")) {
                            try {
                                Object result = method.invoke(loaderInstance, args);
                                dataSource.result.postValue((T) result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (e instanceof InvocationTargetException) {
                                    Throwable targetException = ((InvocationTargetException) e).getTargetException();
                                    if (targetException != null) {
                                        dataSource.error.postValue(targetException);
                                        return;
                                    }
                                }
                                dataSource.error.postValue(e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * DefaultDataSource，采用MutableLiveData实现
     *
     * @param <T>
     */
    private static class DefaultDataSource<T> implements DataSource<T> {

        private final MutableLiveData<T> result = new MutableLiveData<>();
        private final MutableLiveData<Throwable> error = new MutableLiveData<>();

        @Override
        public LiveData<T> result() {
            return result;
        }

        @Override
        public LiveData<Throwable> error() {
            return error;
        }
    }
}
