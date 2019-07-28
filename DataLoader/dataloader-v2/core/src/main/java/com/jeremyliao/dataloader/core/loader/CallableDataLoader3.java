package com.jeremyliao.dataloader.core.loader;


/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with three arguments.
 * @param <T1> the first argument type
 * @param <T2> the second argument type
 * @param <T3> the third argument type
 * @param <R> the result type
 */
public interface CallableDataLoader3<T1, T2, T3, R> extends CallableDataLoader {

    R call(T1 t1, T2 t2, T3 t3) throws Exception;
}
