package com.jeremyliao.dataloader.core.loader;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with two arguments.
 * @param <T1> the first argument type
 * @param <T2> the second argument type
 * @param <R> the result type
 */
public interface LiveDataLoader2<T1, T2, R> extends LiveDataLoader {

    void load(MutableLiveData<R> result, T1 t1, T2 t2);
}
