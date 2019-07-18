package com.jeremyliao.dataloader.utils;

import android.os.Looper;

/**
 * Created by liaohailiang on 2019/3/26.
 */
public final class ThreadUtils {

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
