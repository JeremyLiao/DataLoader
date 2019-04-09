package com.jeremyliao.dataloader.interfaces;

public interface ILoadListener<T> {

    void onDataArrived(T data);
}
