package com.jeremyliao.dataloader.core.loader;


/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with eight arguments.
 * @param <T1> the first argument type
 * @param <T2> the second argument type
 * @param <T3> the third argument type
 * @param <T4> the fourth argument type
 * @param <T5> the fifth argument type
 * @param <T6> the sixth argument type
 * @param <T7> the seventh argument type
 * @param <T8> the eighth argument type
 * @param <R> the result type
 */
public interface CallableDataLoader8<T1, T2, T3, T4, T5, T6, T7, T8, R> extends CallableDataLoader {

    R call(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) throws Exception;
}
