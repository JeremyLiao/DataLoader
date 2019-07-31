package com.jeremyliao.dataloader.demo.dataload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader0;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader1;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData11", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo11 implements LiveDataLoader0<String> {

    @SuppressLint("CheckResult")
    @Override
    public void load(final MutableLiveData<String> result) {
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "DataLoadDemo11 called";
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
