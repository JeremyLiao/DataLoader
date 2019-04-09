package com.jeremyliao.dataloader.interfaces;

public interface ILoadTask<T> {

    /**
     * loadData
     * run in background thread
     *
     * @return T
     */
    T loadData();
}
