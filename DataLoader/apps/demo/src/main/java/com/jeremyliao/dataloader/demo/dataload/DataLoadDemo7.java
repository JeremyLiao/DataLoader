package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader0;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader2;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData7", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo7 implements CallableDataLoader2<String, String, String> {

    @Override
    public String call(String s, String s2) throws Exception {
        return "DataLoadDemo7 called with param: " + s + " | " + s2;
    }
}
