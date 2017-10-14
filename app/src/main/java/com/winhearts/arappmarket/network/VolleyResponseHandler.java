package com.winhearts.arappmarket.network;

import android.os.SystemClock;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.winhearts.arappmarket.model.ReturnMessage;
import com.winhearts.arappmarket.modellevel.ModelLevel;
import com.winhearts.arappmarket.utils.AesUtils;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 网络请求处理类
 * Created by lmh on 2015/11/13.
 */
public abstract class VolleyResponseHandler<T> implements Response.Listener<String>, Response.ErrorListener {
    private final String TAG = "VolleyResponseHandler";
    public static Map<String, Long> diffTimeMap = new Hashtable<String, Long>();
    public static final int RESPONSE_NULL = 3;
    public static final int REQUEST_FAIL = 4;//真正的http请求失败
    public static final int NETWORK_ERROR = 5; //本地没有网络连接
    private boolean isPost;
    private Type mType;
    private int retrytime = 1;
    private UIDataListener<T> uiDataListener;
    private boolean isMixToken = false;
    protected Map<String, String> params;
    protected String url;
    private Object mTag;
    private int initialTimeoutMs = 8000; //超时时间
    private int maxNumRetries = 0; //超时请求次数
    private boolean isAsync = false;

    public VolleyResponseHandler(Type mType) {
        this.mType = mType;
    }

    protected EncryptStringRequest getRequestForGet(String url, Map<String, String> params) {
        if (params == null) {
            return new EncryptStringRequest(url, this, this, initialTimeoutMs, maxNumRetries);
        } else {
            return new EncryptStringRequest(url, params, this, this, initialTimeoutMs, maxNumRetries);

        }
    }

    protected EncryptStringRequest getRequestForPost(String url, Map<String, String> params, boolean isMixToken) {
        return new EncryptStringRequest(Request.Method.POST, url, params, isMixToken, this, this, initialTimeoutMs, maxNumRetries);

    }

    protected EncryptStringRequest getRequestForPostAddHeaders(String url, Map<String, String> params, boolean isMixToken) {
        return new EncryptStringRequest(Request.Method.POST, url, params, isMixToken, this, this, initialTimeoutMs, maxNumRetries) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = super.getHeaders();
                if (map == null || map.isEmpty()) {
                    map = new HashMap<String, String>();
                }
                map.put("Connection", "close");
                return map;
            }
        };
    }

    public void sendGETRequest(String url, Map<String, String> params, boolean isMixToken, UIDataListener<T> uiDataListener) {
        this.isPost = false;
        this.uiDataListener = uiDataListener;
        this.url = url;
        this.params = params;
        this.isMixToken = isMixToken;
        VolleyQueueController.getInstance().getRequestQueue().add(getRequestForGet(url, params).setTag(mTag));
    }

    /**
     * 发送post请求
     *
     * @param url            请求URL
     * @param params         请求参数
     * @param isMixToken     是否对参数进行加密
     * @param uiDataListener 回调接口
     */
    public void sendPostRequest(String url, Map<String, String> params, boolean isMixToken, UIDataListener<T> uiDataListener) {
        this.uiDataListener = uiDataListener;
        this.isPost = true;
        this.url = url;
        this.params = params;
        this.isMixToken = isMixToken;
        //全部改为发送可断开请求
        if (isAsync) {
            VolleyQueueController.getInstance().
                    getmAsyncRequestQueue().add(getRequestForPostAddHeaders(url, params, isMixToken).setTag(mTag));
        } else {
            VolleyQueueController.getInstance().
                    getRequestQueue().add(getRequestForPostAddHeaders(url, params, isMixToken).setTag(mTag));

//        VolleyQueueController.getInstance(context).
//                getRequestQueue().add(getRequestForPost(url, params, isMixToken).setTag(mTag));
        }
    }

    /**
     * 发送返回到子线程的网络请求
     *
     * @param url            访问URL
     * @param params         post请求参数
     * @param isMixToken     是否对参数进行加密
     * @param uiDataListener 返回结果回调监听
     */
    public void sendAsyncPostRequest(String url, Map<String, String> params, boolean isMixToken, UIDataListener<T> uiDataListener) {
        this.uiDataListener = uiDataListener;
        this.isPost = true;
        this.url = url;
        this.params = params;
        this.isAsync = true;
        this.isMixToken = isMixToken;
        //全部改为发送可断开请求,取消服务器的长链接
        VolleyQueueController.getInstance().
                getmAsyncRequestQueue().add(getRequestForPostAddHeaders(url, params, isMixToken).setTag(mTag));

//        VolleyQueueController.getInstance(context).
//                getRequestQueue().add(getRequestForPost(url, params, isMixToken).setTag(mTag));
    }


    private void reSendRequest() {
        if (isPost) {
            if (isAsync) {
                VolleyQueueController.getInstance().
                        getmAsyncRequestQueue().add(getRequestForPostAddHeaders(url, params, isMixToken).setTag(mTag));
            } else {
                VolleyQueueController.getInstance().
                        getRequestQueue().add(getRequestForPostAddHeaders(url, params, isMixToken).setTag(mTag));
//            VolleyQueueController.getInstance(context).
//                    getRequestQueue().add(getRequestForPost(url, params, isMixToken).setTag(mTag));
            }
        } else {
            VolleyQueueController.getInstance().getRequestQueue().add(getRequestForGet(url, params).setTag(mTag));
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        disposeVolleyError(error);
    }

    protected abstract void disposeVolleyError(VolleyError error);

    protected abstract void disposeResponse(T response);

    public void disposeString(String src) {
        if (uiDataListener != null) {
            uiDataListener.onStringChanged(src);
        }
    }

    protected void retimeQuest() {
        reSendRequest();
    }

    protected void notifyDataChanged(T data) {
        if (uiDataListener != null) {
            uiDataListener.onDataChanged(data);
        }
    }

    protected void notifyErrorHappened(int errorCode, Exception errorMessage) {
        if (uiDataListener != null) {
            uiDataListener.onErrorHappened(errorCode, errorMessage);
        }
    }

    protected void onVolleyError(int errorCode, Exception errorMessage) {
        if (uiDataListener != null) {
            uiDataListener.onVolleyError(errorCode, errorMessage);
        }
        if (!TextUtils.isEmpty(url) && errorCode == REQUEST_FAIL) {
            if (url.contains("/ams/")) {
                ModelLevel.setLogMessage(errorCode, errorMessage.getMessage(), "AMS", url + params.toString());
            } else if (url.contains("/vms/")) {
                ModelLevel.setLogMessage(errorCode, errorMessage.getMessage(), "VMS", url + params.toString());
            } else {
                ModelLevel.setLogMessage(errorCode, errorMessage.getMessage(), "other", url + params.toString());
            }
        }
    }

    @Override
    public void onResponse(String response) {
        if (response != null) {
            try {
                Gson gson = new Gson();
                ReturnMessage msg = gson.fromJson(response, ReturnMessage.class);
                String returnContent = msg.getContent();
                int returnCode = msg.getReturnCode();
                String returnMsg = msg.getReturnMsg();
                switch (returnCode) {
                    case ReturnMessage.SUCCESS:
                        int returnType = msg.getType();
                        if (!TextUtils.isEmpty(returnContent)) {
                            if (returnType == 1) {
                                returnContent = AesUtils.decryptBase64(returnContent, AesUtils.PASSWORD);
                            }
                            disposeString(returnContent);
                        } else {
                            disposeString(returnMsg);
                        }
                        T t = null;
                        if (!TextUtils.isEmpty(returnContent)) {
                            t = gson.fromJson(returnContent, mType);
                        }
                        disposeResponse(t);
                        break;

                    case ReturnMessage.TIME_OFF:
                        // 101超时，指本地时间和服务器时间相差太大，得到该返回值会做修正
                        if (!TextUtils.isEmpty(returnContent)) {
                            long dstTime = Long.parseLong(returnContent);
                            long localTime = SystemClock.elapsedRealtime() / 1000;
                            long diffTime;
                            diffTime = dstTime - localTime;
                            String host = Util.getHost(url);
                            if (host != null) {
                                diffTimeMap.put(host, diffTime);
                            }
                            if (retrytime > 0) {
                                retimeQuest();
                                retrytime--;
                            } else {
                                Exception paramException = null;
                                if (returnMsg != null) {
                                    paramException = new Exception(returnMsg);
                                } else {
                                    paramException = new Exception("服务器时间不匹配" + mType);
                                }
                                notifyErrorHappened(returnCode, paramException);
                            }
                        } else {
                            Exception responseNull = new Exception("returnContent==null!");
                            LoggerUtil.i("onResponse", url + "returnContent==null!");
                            notifyErrorHappened(RESPONSE_NULL, responseNull);
                        }
                        break;
                    case ReturnMessage.ILLEGALITY:
                        Exception illException = null;
                        if (returnContent != null) {
                            illException = new Exception(returnContent);
                        } else {
                            illException = new Exception("ILLEGALITY" + mType);
                        }
                        notifyErrorHappened(returnCode, illException);
                        break;
                    case ReturnMessage.PARAM_ERROR:
                        Exception paramException = null;
                        if (returnMsg != null) {
                            paramException = new Exception(returnMsg);
                        } else {
                            paramException = new Exception("PARAM_ERROR" + mType);
                        }
                        notifyErrorHappened(returnCode, paramException);
                        break;
                    default:
                        Exception otherException = null;
                        if (!TextUtils.isEmpty(returnMsg)) {
                            otherException = new Exception(returnMsg);
                        } else {
                            otherException = new Exception("PARAM_ERROR" + mType);
                        }
                        notifyErrorHappened(returnCode, otherException);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LoggerUtil.i("onResponse", e.getMessage() + url + response);
                notifyErrorHappened(RESPONSE_NULL, e);
            }
        } else {
            Exception responseNull = new Exception("Response is null!");
            LoggerUtil.i("onResponse", url + "Response is null!");
            notifyErrorHappened(RESPONSE_NULL, responseNull);
        }
    }

    public VolleyResponseHandler<T> setRetrytime(int retime) {
        this.retrytime = retime;
        return this;
    }

    public void setRequestTag(Object object) {
        mTag = object;

    }

    public void setTimeOut(int initialTimeoutMs, int maxNumRetries) {
        this.initialTimeoutMs = initialTimeoutMs;
        this.maxNumRetries = maxNumRetries;
    }


    /**
     * 返回是否在子线程中
     *
     * @param isAsync true返回在子线程 默认返回到主线程
     */
    public void isAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }
}
