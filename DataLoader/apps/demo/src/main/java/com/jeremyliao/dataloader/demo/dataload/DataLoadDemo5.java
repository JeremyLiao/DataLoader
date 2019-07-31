package com.jeremyliao.dataloader.demo.dataload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.LiveDataLoader1;
import com.jeremyliao.dataloader.demo.bean.CommonParam;
import com.jeremyliao.dataloader.demo.bean.CommonResult;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData5", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo5 implements LiveDataLoader1<CommonParam<String>, CommonResult<String>> {

    @SuppressLint("CheckResult")
    @Override
    public void load(final MutableLiveData<CommonResult<String>> result, CommonParam<String> param) {
        Observable.just(param)
                .map(new Function<CommonParam<String>, String>() {
                    @Override
                    public String apply(CommonParam<String> stringCommonParam) throws Exception {
                        return stringCommonParam.param;
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return "DataLoadDemo5 called with param: " + s;
                    }
                })
                .map(new Function<String, CommonResult<String>>() {
                    @Override
                    public CommonResult<String> apply(String s) throws Exception {
                        CommonResult<String> result1 = new CommonResult<>();
                        result1.data = s;
                        return result1;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonResult<String>>() {
                    @Override
                    public void accept(CommonResult<String> r) throws Exception {
                        result.setValue(r);
                    }
                });
    }
}
