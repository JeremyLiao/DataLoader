package com.jeremyliao.dataloader.interfaces;

public interface LoadListener<T> {

    void onDataArrived(T data);
}
