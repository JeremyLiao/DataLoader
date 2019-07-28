package com.jeremyliao.dataloader.core.loader;


/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with one argument.
 * @param <T> the first argument type
 * @param <R> the result type
 */
public interface CallableDataLoader1<T, R> extends CallableDataLoader {

    R call(T param) throws Exception;
}
