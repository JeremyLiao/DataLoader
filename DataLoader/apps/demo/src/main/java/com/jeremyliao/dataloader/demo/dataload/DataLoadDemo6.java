package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader0;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader1;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData6", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo6 implements CallableDataLoader0<String> {

    @Override
    public String call() throws Exception {
        return "DataLoadDemo6 called";
    }
}
