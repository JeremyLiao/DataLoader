package com.jeremyliao.dataloader.demo.dataload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader0;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader2;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData12", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo12 implements LiveDataLoader2<String, String, String> {

    @SuppressLint("CheckResult")
    @Override
    public void load(final MutableLiveData<String> result, final String s, final String s2) {
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "DataLoadDemo12 called with params: " + s + " | " + s2;
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
