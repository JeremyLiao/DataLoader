package com.jeremyliao.dataloader.demo.application;

import android.app.Application;

import com.jeremyliao.dataloader.core.DataLoader;

/**
 * Created by liaohailiang on 2019-07-26.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataLoader.init();
    }
}
