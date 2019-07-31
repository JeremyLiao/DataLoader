package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader2;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader3;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData8", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo8 implements CallableDataLoader3<String, String, String, String> {

    @Override
    public String call(String s, String s2, String s3) throws Exception {
        return "DataLoadDemo8 called with param: " + s + " | " + s2 + " | " + s3;
    }
}
