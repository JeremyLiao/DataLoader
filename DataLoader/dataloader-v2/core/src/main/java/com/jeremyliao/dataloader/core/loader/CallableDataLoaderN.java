package com.jeremyliao.dataloader.core.loader;


/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a vector-argument function.
 * @param <R> the result type
 */
public interface CallableDataLoaderN<R> extends CallableDataLoader {

    R call(Object... args) throws Exception;
}
