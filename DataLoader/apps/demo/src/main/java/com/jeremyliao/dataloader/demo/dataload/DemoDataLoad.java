package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader1;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getDemoData", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DemoDataLoad implements CallableDataLoader1<String, String> {

    @Override
    public String call(String param) throws Exception {
        return "helloworld";
    }
}
