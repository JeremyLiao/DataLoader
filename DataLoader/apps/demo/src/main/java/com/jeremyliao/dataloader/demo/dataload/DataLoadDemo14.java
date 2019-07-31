package com.jeremyliao.dataloader.demo.dataload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader3;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader4;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData14", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo14 implements LiveDataLoader4<String, String, String, String, String> {

    @SuppressLint("CheckResult")
    @Override
    public void load(final MutableLiveData<String> result, final String s, final String s2, final String s3, final String s4) {
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "DataLoadDemo14 called with params: " + s + " | " + s2 + " | " + s3 + " | " + s4;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        result.setValue(s);
                    }
                });
    }
}
