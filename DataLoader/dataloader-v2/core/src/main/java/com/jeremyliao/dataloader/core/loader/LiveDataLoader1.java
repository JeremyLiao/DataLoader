package com.jeremyliao.dataloader.core.loader;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with one argument.
 * @param <T> the first argument type
 * @param <R> the result type
 */
public interface LiveDataLoader1<T, R> extends LiveDataLoader {

    void load(MutableLiveData<R> result, T param);
}
