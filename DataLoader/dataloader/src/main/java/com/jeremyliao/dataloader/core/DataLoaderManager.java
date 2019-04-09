package com.jeremyliao.dataloader.core;

public final class DataLoaderManager {

    private static class SingletonHolder {
        private static final DataLoaderManager INSTANCE = new DataLoaderManager();
    }

    public static DataLoaderManager get() {
        return SingletonHolder.INSTANCE;
    }

    private DataLoaderManager() {
    }
}
