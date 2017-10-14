package com.winhearts.arappmarket.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求工具类
 */
public class RequestUtil {

    public static final String prefix = "281031764343127189610010480095250822367";

    public static final int safeCode = 1;

    public static Map<String, String> getRequestParam(String requestStr) {
        Map<String, String> params = new HashMap<String, String>();
        if (requestStr != null) {
            params.put("clientInfo", requestStr);
        }
//		params.put("t", String.valueOf(Util.getAmendCurrentTime()));
        return params;
    }

    public static Map<String, String> getRequestParam(String key, String requestStr) {
        Map<String, String> params = new HashMap<String, String>();
        if (requestStr != null) {
            params.put(key, requestStr);
        }
        return params;
    }
}
