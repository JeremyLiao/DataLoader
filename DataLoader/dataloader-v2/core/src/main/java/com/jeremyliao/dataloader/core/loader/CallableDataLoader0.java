package com.jeremyliao.dataloader.core.loader;


import java.util.concurrent.Callable;

/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with zero arguments.
 * @param <R> the result type
 */
public interface CallableDataLoader0<R> extends CallableDataLoader, Callable<R> {

    @Override
    R call() throws Exception;
}
