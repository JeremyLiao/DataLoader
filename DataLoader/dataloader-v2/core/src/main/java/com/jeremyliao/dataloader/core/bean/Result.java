package com.jeremyliao.dataloader.core.bean;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

/**
 * Created by liaohailiang on 2019-07-22.
 */
public class Result<T> {
    public final LiveData<T> result = new MutableLiveData<>();

    public final LiveData<Throwable> error = new MutableLiveData<>();
}
