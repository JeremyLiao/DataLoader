package com.jeremyliao.dataloader.core;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.jeremyliao.dataloader.interfaces.LoadListener;
import com.jeremyliao.dataloader.interfaces.LoadTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DataLoadTask<T> implements Runnable {

    final MutableLiveData<T> liveData = new MutableLiveData<>();
    final LoadTask<T> loadTask;
    private Map<LoadListener<T>, Observer<T>> listenerObserverMap = new ConcurrentHashMap<>();

    DataLoadTask(@NonNull LoadTask<T> loadTask) {
        this.loadTask = loadTask;
    }

    @MainThread
    void addListener(@NonNull LifecycleOwner owner, @NonNull final LoadListener<T> listener) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                listener.onDataArrived(t);
            }
        });
    }

    @MainThread
    void addListener(@NonNull final LoadListener<T> listener) {
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                listener.onDataArrived(t);
            }
        };
        listenerObserverMap.put(listener, observer);
        liveData.observeForever(observer);
    }

    @MainThread
    void removeListener(@NonNull final LoadListener<T> listener) {
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
