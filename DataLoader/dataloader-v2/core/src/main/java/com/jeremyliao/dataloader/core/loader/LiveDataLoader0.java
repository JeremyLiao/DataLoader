package com.jeremyliao.dataloader.core.loader;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a function with one argument.
 *
 * @param <R> the result type
 */
public interface LiveDataLoader0<R> extends LiveDataLoader {

    void load(MutableLiveData<R> result);
}
