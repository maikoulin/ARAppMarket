package com.winhearts.arappmarket.modellevel;

import android.content.Context;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.EvaluateEntity;
import com.winhearts.arappmarket.model.ReplyResEntity;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 评论相关请求封装
 * Created by lmh on 2015/8/9.
 */
public class ModelevelEvaluate {

    private final static String TAG = "ModelevelEvaluate";

    public static void presentEvaluate(final Context context, final Map<String, String> map, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.PRESENT_EVALUATE);
        Type type = new TypeToken<String>() {
        }.getType();
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, errorMessage.getMessage());
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void getEvaluateList(Context context, Map<String, String> map, final ModeUserErrorCode<EvaluateEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.GET_EVALUATE);
        Type type = new TypeToken<EvaluateEntity>() {
        }.getType();
        map.put("pageSize", "20");
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        SubVolleyResponseHandler<EvaluateEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<EvaluateEntity>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<EvaluateEntity>() {
            @Override
            public void onDataChanged(EvaluateEntity data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void getReplyList(Context context, Map<String, String> map, final ModeUserErrorCode<ReplyResEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.GET_REPLY_LIST);
        Type type = new TypeToken<ReplyResEntity>() {
        }.getType();
        map.put("pageSize", "20");
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        SubVolleyResponseHandler<ReplyResEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<ReplyResEntity>() {
            @Override
            public void onDataChanged(ReplyResEntity data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void commentReply(Context context, Map<String, String> map, final ModeUserErrorCode<String> user) {
        final String url = Util.getUrl(context, ModeUrl.COMMENT_REPLY);
        Type type = new TypeToken<ReplyResEntity>() {
        }.getType();
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }
}
