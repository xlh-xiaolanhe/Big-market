package com.xiaolanhe.types.utils;

import com.google.gson.Gson;

/**
 *@author: xiaolanhe
 *@createDate: 2024/9/13 17:14
 */
public class GsonUtils {

    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
