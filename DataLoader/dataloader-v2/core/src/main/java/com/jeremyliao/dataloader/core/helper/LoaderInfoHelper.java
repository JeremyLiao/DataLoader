package com.jeremyliao.dataloader.core.helper;

import com.jeremyliao.dataloader.core.common.Const;

/**
 * Created by liaohailiang on 2019-07-31.
 */
public class LoaderInfoHelper {

    private LoaderInfoHelper() {
    }

    public static String getLoaderInfoJson() throws Exception {
        return (String) Class.forName(Const.SERVICE_LOADER_INIT)
                .getMethod(Const.INIT_METHOD)
                .invoke(null);
    }
}
