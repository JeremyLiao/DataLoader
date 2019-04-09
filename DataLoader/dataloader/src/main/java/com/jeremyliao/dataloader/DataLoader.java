package com.jeremyliao.dataloader;

import android.arch.lifecycle.LifecycleOwner;

import com.jeremyliao.dataloader.interfaces.ILoadListener;
import com.jeremyliao.dataloader.interfaces.ILoadTask;

public final class DataLoader {

    public static <T> int load(ILoadTask<T> task) {
    }

    public static <T> int load(ILoadTask<T> task, ILoadListener<T> listener) {
    }

    public static <T> int load(LifecycleOwner owner, ILoadTask<T> task, ILoadListener<T> listener) {
    }
}
