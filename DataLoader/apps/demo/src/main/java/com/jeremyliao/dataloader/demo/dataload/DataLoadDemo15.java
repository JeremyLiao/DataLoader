package com.jeremyliao.dataloader.demo.dataload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader4;
import com.jeremyliao.dataloader.core.loader.LiveDataLoaderN;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData15", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo15 implements LiveDataLoaderN<String> {

    @SuppressLint("CheckResult")
    @Override
    public void load(final MutableLiveData<String> result, final Object... args) {
        Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        StringBuilder sb = new StringBuilder();
                        if (args != null && args.length > 0) {
                            for (Object arg : args) {
                                sb.append(arg).append(";");
                            }
                        }
                        return "DataLoadDemo15 called with params: " + sb.toString();
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
