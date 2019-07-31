package com.jeremyliao.dataloader.demo.dataload;

import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.core.loader.CallableDataLoader1;
import com.jeremyliao.dataloader.demo.bean.CommonParam;
import com.jeremyliao.dataloader.demo.bean.CommonResult;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@DataLoad(method = "getData3", target = "com.jeremyliao.dataloader.demo.dataload.DemoDataSource")
public class DataLoadDemo3 implements CallableDataLoader1<CommonParam<String>, CommonResult<String>> {

    @Override
    public CommonResult<String> call(CommonParam<String> param) throws Exception {
        CommonResult<String> result = new CommonResult<>();
        result.data = "DataLoadDemo3 called with param: " + param.param;
        return result;
    }
}
