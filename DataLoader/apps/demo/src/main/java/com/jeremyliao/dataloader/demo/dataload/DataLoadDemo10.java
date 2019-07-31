package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader4;
import com.jeremyliao.dataloader.core.loader.CallableDataLoaderN;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData10", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo10 implements CallableDataLoaderN<String> {

    @Override
    public String call(Object... args) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                sb.append(arg).append(";");
            }
        }
        return "DataLoadDemo10 called with param: " + sb.toString();
    }
}
