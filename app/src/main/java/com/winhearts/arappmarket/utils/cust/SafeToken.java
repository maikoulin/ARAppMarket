package com.winhearts.arappmarket.utils.cust;

import android.text.TextUtils;
import android.util.Log;

import com.winhearts.arappmarket.utils.Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hongxingshi
 * Date: 14-11-20
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class SafeToken {
    private final static String TAG = "SafeToken";
    public static final String INPUT_CHARSET = "utf-8";
    public static final String SECURITY_VERSION_STRING = "v";
    public static final String TIME_STAMP = "t";
    public static final String TOKEN = "token";

    public final static String PREFIX = "281031764343127189610010480095250822367";// md5 prefix
    public final static int SAFE_VERSION_KEY = 1;//安全协议版本
    public final static int TIME_LINE = 30;// 超时限度

    private int v;
    private String t;
//    private String token;



    public SafeToken(int v, String t) {
        this.v = v;
        this.t = t;

    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }


    public Map<String,String> paraFilter(Map<String,String> params){
        Map<String, String> result = new HashMap<String, String>();

        if (params.isEmpty()) {
            return result;
        }
        String value = null ;
        String key = null ;
        for (Map.Entry<String,String> entry : params.entrySet()) {
            value = entry.getValue();
            key = entry.getKey();
            if (TextUtils.isEmpty(value) || TOKEN.equalsIgnoreCase(key)
                    || SECURITY_VERSION_STRING.equals(key) || TIME_STAMP.equals(key)) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder builder = new StringBuilder(1024);
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            builder.append(params.get(keys.get(i)));
        }

        return builder.toString();
    }

    public String sign(String key , Map<String, String> params){
        if(params == null || params.isEmpty()){
            return "";
        }
        String preSignStr = key + createLinkString(paraFilter(params)) + getV() + getT();

       return preSignStr;

    }

    public String signMd5(String key , Map<String, String> params){
        if(params == null || params.isEmpty()){
            return "";
        }
        String preSignStr = key + createLinkString(paraFilter(params)) + getV() + getT();

        try {
            return Util.getMD5(preSignStr);
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
            Log.e(TAG, e.toString());
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return null;
        }

    }
}
