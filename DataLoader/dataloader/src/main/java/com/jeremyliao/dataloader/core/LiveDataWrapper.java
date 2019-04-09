package com.jeremyliao.dataloader.core;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.jeremyliao.dataloader.interfaces.ILoadListener;
import com.jeremyliao.dataloader.interfaces.ILoadTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class LiveDataWrapper<T> implements Runnable {

    final MutableLiveData<T> liveData = new MutableLiveData<>();
    final ILoadTask<T> loadTask;
    private Map<ILoadListener<T>, Observer<T>> listenerObserverMap = new ConcurrentHashMap<>();

    LiveDataWrapper(@NonNull ILoadTask<T> loadTask) {
        this.loadTask = loadTask;
    }

    void addListener(@NonNull LifecycleOwner owner, @NonNull final ILoadListener<T> listener) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                listener.onDataArrived(t);
            }
        });
    }

    void addListener(@NonNull final ILoadListener<T> listener) {
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                listener.onDataArrived(t);
            }
        };
        listenerObserverMap.put(listener, observer);
        liveData.observeForever(observer);
    }

    void removeListener(@NonNull final ILoadListener<T> listener) {
        if (listenerObserverMap.containsKey(listener)) {
            Observer<T> observer = listenerObserverMap.remove(listener);
            liveData.removeObserver(observer);
        }
    }

    @Override
    @WorkerThread
    public void run() {
        T t = loadTask.loadData();
        liveData.postValue(t);
    }
}
