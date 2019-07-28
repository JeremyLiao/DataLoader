package com.jeremyliao.dataloader.core.loader;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by liaohailiang on 2019-07-19.
 * Represents a vector-argument function.
 * @param <R> the result type
 */
public interface LiveDataLoaderN< R> extends LiveDataLoader {

    void load(MutableLiveData<R> result, Object... args);
}
