package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader1;
import com.jeremyliao.dataloader.demo.bean.DemoParam;
import com.jeremyliao.dataloader.demo.bean.DemoResult;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData2", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo2 implements CallableDataLoader1<DemoParam, DemoResult> {

    @Override
    public DemoResult call(DemoParam param) throws Exception {
        DemoResult result = new DemoResult();
        result.name = param.name;
        result.no = param.no;
        return result;
    }
}
