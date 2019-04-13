package com.jeremyliao.dataloader;

import android.arch.lifecycle.LifecycleOwner;

import com.jeremyliao.dataloader.core.DataLoaderManager;
import com.jeremyliao.dataloader.interfaces.LoadListener;
import com.jeremyliao.dataloader.interfaces.LoadTask;

import java.util.concurrent.ExecutorService;

public final class DataLoader {

    /**
     * 创建一个load数据的任务
     * @param task
     * @param <T>
     * @return 任务id
     */
    public static <T> int load(LoadTask<T> task) {
        int id = DataLoaderManager.get().addTask(task);
        return id;
    }

    /**
     * 创建一个load数据的任务，当数据加载完成的时候，回调listener
     * @param task
     * @param listener
     * @param <T>
     * @return 任务id
     */
    public static <T> int load(LoadTask<T> task, LoadListener<T> listener) {
        int id = DataLoaderManager.get().addTask(task);
        DataLoaderManager.get().addListener(id, null, listener);
        return id;
    }

    /**
     * 创建一个load数据的任务，当数据加载完成的时候，回调listener
     * listener具有生命周期感知能力，只需要传入一个LifecycleOwner，一般是Activity
     * 这样就不用关心何时把这个listener remove掉了
     * @param owner
     * @param task
     * @param listener
     * @param <T>
     * @return 任务id
     */
    public static <T> int load(LifecycleOwner owner, LoadTask<T> task, LoadListener<T> listener) {
        int id = DataLoaderManager.get().addTask(task);
        DataLoaderManager.get().addListener(id, owner, listener);
        return id;
    }

    /**
     * 注册一个数据监听器LoadListener
     * @param id
     * @param listener
     * @param <T>
     */
    public static <T> void listen(int id, LoadListener<T> listener) {
        DataLoaderManager.get().addListener(id, null, listener);
    }

    /**
     * 注册一个数据监听器LoadListener
     * listener具有生命周期感知能力，只需要传入一个LifecycleOwner，一般是Activity
     * @param id
     * @param owner
     * @param listener
     * @param <T>
     */
    public static <T> void listen(int id, LifecycleOwner owner, LoadListener<T> listener) {
        DataLoaderManager.get().addListener(id, owner, listener);
    }

    /**
     * remove调一个LoadListener
     * @param id
     * @param listener
     * @param <T>
     */
    public static <T> void removeListener(int id, LoadListener<T> listener) {
        DataLoaderManager.get().removeListener(id, listener);
    }

    /**
     * 判断一个task id是否存在
     * @param id
     * @return
     */
    public static boolean exists(int id) {
        return DataLoaderManager.get().exists(id);
    }

    /**
     * 重新load数据
     * @param id
     */
    public static void refresh(int id) {
        DataLoaderManager.get().refresh(id);
    }

    /**
     * 设置自定义的ExecutorService
     * @param executor
     */
    public void setExecutor(ExecutorService executor) {
        DataLoaderManager.get().setExecutor(executor);
    }
}
