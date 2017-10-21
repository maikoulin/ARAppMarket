package com.winhearts.arappmarket.network;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.Utils;
import com.winhearts.arappmarket.utils.cust.SafeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmh on 2015/11/11.
 */
public class EncryptStringRequest extends StringRequest {
    private Priority mPriority = Priority.HIGH;
    public static final String prefix = "61919538434965145604794060779633385472";
    public static final int safeCode = 1;
    private Map<String, String> params;

    public EncryptStringRequest(int method, String url,
                                Map<String, String> postParams, boolean isMixToken, Response.Listener<String> listener,
                                Response.ErrorListener errorListener, int initialTimeoutMs, int maxNumRetries) {

        super(method, url, listener, errorListener);
        params = addOthersParams(postParams, isMixToken, url);
        this.setShouldCache(false);
        //设置超时时间和超时请求次数
        setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public EncryptStringRequest(String url, Map<String, String> params,
                                Response.Listener<String> listener, Response.ErrorListener errorListener, int initialTimeoutMs, int maxNumRetries) {
        this(Method.GET, urlBuilder(url, params), null, false, listener, errorListener, initialTimeoutMs, maxNumRetries);
    }

    public EncryptStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, int initialTimeoutMs, int maxNumRetries) {
        this(Method.GET, url, null, false, listener, errorListener, initialTimeoutMs, maxNumRetries);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    private static String urlBuilder(String url, Map<String, String> params) {
        if (params != null && params.size() > 0) {
            String paramsEncoding = "UTF-8";
            StringBuilder encodedParams = new StringBuilder();
            encodedParams.append("?");
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(),
                            paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(),
                            paramsEncoding));
                    encodedParams.append('&');
                }
                encodedParams.deleteCharAt(encodedParams.length() - 1);
                return url + encodedParams.toString();
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: "
                        + paramsEncoding, uee);
            }
        }
        return null;
    }

    private String addOthersParams(String urlString, boolean isMixToken) {
        long t = Util.getAmendCurrentTime(urlString);
        String preMD5;
        String token;
        StringBuilder currentStr = new StringBuilder(urlString);
        if (isMixToken) {
            Map<String, String> map = new HashMap<String, String>();
            map.putAll(params);
            map.put("mac", MacUtil.getMacAddress());
            SafeToken safeToken = new SafeToken(safeCode, String.valueOf(t));
            preMD5 = safeToken.sign(prefix, map);
        } else {
            preMD5 = prefix + safeCode + t;
        }
        try {
            token = Util.getMD5(preMD5);
            currentStr.append("?v=").append(URLEncoder.encode(String.valueOf(safeCode), "utf-8"))
                    .append("&mac=").append(URLEncoder.encode(MacUtil.getMacAddress(), "utf-8"))
                    .append("&token=").append(URLEncoder.encode(token, "utf-8"))
                    .append("&t=").append(URLEncoder.encode(String.valueOf(t), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentStr.toString();
    }

    private Map<String, String> addOthersParams(Map<String, String> postParams, boolean isMixToken, String url) {
        long t = Util.getAmendCurrentTime(url);
        String preMD5;
        String token;
        if (isMixToken) {
            Map<String, String> map = new HashMap<String, String>();
            map.putAll(postParams);
            map.put("mac", MacUtil.getMacAddress());
            SafeToken safeToken = new SafeToken(safeCode, String.valueOf(t));
            preMD5 = safeToken.sign(prefix, map);
        } else {
            preMD5 = prefix + safeCode + t;
        }
        try {
            token = Util.getMD5(preMD5);
            postParams.put("v", String.valueOf(safeCode));
            postParams.put("mac", MacUtil.getMacAddress());
            postParams.put("token", token);
            postParams.put("t", String.valueOf(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerUtil.d("request", url +":"+ Utils.mapToString(postParams));
        return postParams;
    }
}
