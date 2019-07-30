package com.jeremyliao.dataloader.core.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by liaohailiang on 2019-07-28.
 */
public class GenericsUtils {

    public static Class getGenericType(Class clazz) {
        return getGenericType(clazz, 0);
    }

    public static Class getGenericType(Class clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getClass().getGenericSuperclass();
        Type[] genIType = clazz.getGenericInterfaces();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}
