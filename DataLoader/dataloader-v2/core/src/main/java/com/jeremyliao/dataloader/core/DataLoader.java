package com.jeremyliao.dataloader.core;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyliao.dataloader.base.utils.EncryptUtils;
import com.jeremyliao.dataloader.core.common.Const;
import com.jeremyliao.dataloader.core.loader.BaseDataLoader;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader;
import com.jeremyliao.dataloader.core.source.DataSource;
import com.jeremyliao.dataloader.core.utils.GenericsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaohailiang on 2019-07-25.
 */
public class DataLoader {

    private static final String TAG = "DataLoader";
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();

    private static Map<String, String> loaderInfoMap;
    private static Map<String, Object> loaderInfoCache = new HashMap<>();
    private static Map<Class, Object> proxyCache = new HashMap<>();
    private static ExecutorService executor;

    public static void init() {
        doInit();
    }

    private static void doInit() {
        try {
            Gson gson = new Gson();
            String json = (String) Class.forName(Const.SERVICE_LOADER_INIT)
                    .getMethod(Const.INIT_METHOD)
                    .invoke(null);
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            loaderInfoMap = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static class InterfaceHandler implements InvocationHandler {

        private final Class interfaceType;

        InterfaceHandler(Class interfaceType) {
            this.interfaceType = interfaceType;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "method: " + method);
            String key = getKey(method);
            Log.d(TAG, "key: " + key);
            Log.d(TAG, "loaderInfoCache: " + loaderInfoCache);
            Log.d(TAG, "loaderInfoMap: " + loaderInfoMap);
            if (!loaderInfoCache.containsKey(key)) {
                if (loaderInfoMap.containsKey(key)) {
                    String loaderClassName = loaderInfoMap.get(key);
                    Class loaderClass = Class.forName(loaderClassName);
                    Object loaderInstance = loaderClass.newInstance();
                    loaderInfoCache.put(key, loaderInstance);
                }
            }
            Object loaderInstance = loaderInfoCache.get(key);
            if (loaderInstance == null) {
                return null;
            }
            if (!(loaderInstance instanceof BaseDataLoader)) {
                return null;
            }

            if (loaderInstance instanceof CallableDataLoader) {
                DefaultDataSource dataSource = new DefaultDataSource();
                DataLoadTask task = new DataLoadTask(dataSource, loaderInstance, args);
                getExecutor().execute(task);
                return dataSource;
            } else if (loaderInstance instanceof LiveDataLoader) {
                DefaultDataSource dataSource = new DefaultDataSource();
                processLoad(dataSource, loaderInstance, args);
                return dataSource;
            }
            return null;
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
                            method.invoke(loaderInstance, args);
                        } catch (Exception e) {
                            dataSource.error.postValue(e);
                        }

                    }
                }
            }
        }
    }

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
                                dataSource.error.postValue(e);
                            }

                        }
                    }
                }
            }
        }
    }

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
