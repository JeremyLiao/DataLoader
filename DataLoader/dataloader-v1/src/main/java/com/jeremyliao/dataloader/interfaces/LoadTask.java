package com.jeremyliao.dataloader.interfaces;

public interface LoadTask<T> {

    /**
     * loadData
     * run in background thread
     *
     * @return T
     */
    T loadData();
}
