package com.jeremyliao.dataloader.core.source;

import android.arch.lifecycle.LiveData;

/**
 * Created by liaohailiang on 2019-07-23.
 */
public interface DataSource<T> {

    LiveData<T> result();

    LiveData<Throwable> error();
}
