package com.jeremyliao.dataloader;

import android.arch.lifecycle.LifecycleOwner;

import com.jeremyliao.dataloader.core.DataLoaderManager;
import com.jeremyliao.dataloader.interfaces.LoadListener;
import com.jeremyliao.dataloader.interfaces.LoadTask;

public final class DataLoader {

    public static <T> int load(LoadTask<T> task) {
        int id = DataLoaderManager.get().addTask(task);
        return id;
    }

    public static <T> int load(LoadTask<T> task, LoadListener<T> listener) {
        int id = DataLoaderManager.get().addTask(task);
        DataLoaderManager.get().addListener(id, null, listener);
        return id;
    }

    public static <T> int load(LifecycleOwner owner, LoadTask<T> task, LoadListener<T> listener) {
        int id = DataLoaderManager.get().addTask(task);
        DataLoaderManager.get().addListener(id, owner, listener);
        return id;
    }

    public static <T> void listen(int id, LoadListener<T> listener) {
        DataLoaderManager.get().addListener(id, null, listener);
    }

    public static <T> void listen(int id, LifecycleOwner owner, LoadListener<T> listener) {
        DataLoaderManager.get().addListener(id, owner, listener);
    }

    public static <T> void removeListener(int id, LoadListener<T> listener) {
        DataLoaderManager.get().removeListener(id, listener);
    }

    public static boolean exists(int id) {
        return DataLoaderManager.get().exists(id);
    }

    public static void refresh(int id) {
        DataLoaderManager.get().refresh(id);
    }
}
