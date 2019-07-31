package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader3;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader4;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData9", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo9 implements CallableDataLoader4<String, String, String, String, String> {

    @Override
    public String call(String s, String s2, String s3, String s4) throws Exception {
        return "DataLoadDemo9 called with param: " + s + " | " + s2 + " | " + s3 + " | " + s4;
    }
}
