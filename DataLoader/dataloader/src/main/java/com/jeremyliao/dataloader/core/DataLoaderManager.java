package com.jeremyliao.dataloader.core;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Looper;

import com.jeremyliao.dataloader.interfaces.LoadListener;
import com.jeremyliao.dataloader.interfaces.LoadTask;
import com.jeremyliao.dataloader.utils.ThreadUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class DataLoaderManager {

    private static class SingletonHolder {
        private static final DataLoaderManager INSTANCE = new DataLoaderManager();
    }

    public static DataLoaderManager get() {
        return SingletonHolder.INSTANCE;
    }

    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();

    private final Map<Integer, DataLoadTask> taskMap = new ConcurrentHashMap<>();
    private final AtomicInteger idMaker = new AtomicInteger(0);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executor;

    private DataLoaderManager() {
    }

    public <T> int addTask(LoadTask<T> task) {
        DataLoadTask<T> dataLoadTask = new DataLoadTask<>(task);
        int id = idMaker.incrementAndGet();
        taskMap.put(id, dataLoadTask);
        getExecutor().execute(dataLoadTask);
        return id;
    }

    public <T> void addListener(int id, final LifecycleOwner owner, final LoadListener<T> listener) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        if (listener == null) {
            return;
        }
        final DataLoadTask dataLoadTask = taskMap.get(id);
        if (ThreadUtils.isMainThread()) {
            if (owner == null) {
                dataLoadTask.addListener(listener);
            } else {
                dataLoadTask.addListener(owner, listener);
            }
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (owner == null) {
                        dataLoadTask.addListener(listener);
                    } else {
                        dataLoadTask.addListener(owner, listener);
                    }
                }
            });
        }

    }

    public <T> void removeListener(int id, final LoadListener<T> listener) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        if (listener == null) {
            return;
        }
        final DataLoadTask dataLoadTask = taskMap.get(id);
        if (ThreadUtils.isMainThread()) {
            dataLoadTask.removeListener(listener);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    dataLoadTask.removeListener(listener);
                }
            });
        }
    }

    private ExecutorService getExecutor() {
        if (executor != null) {
            return executor;
        }
        return DEFAULT_EXECUTOR;
    }

    public boolean exists(int id) {
        return taskMap.containsKey(id);
    }

    public void refresh(int id) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        DataLoadTask dataLoadTask = taskMap.get(id);
        getExecutor().execute(dataLoadTask);
    }
}
