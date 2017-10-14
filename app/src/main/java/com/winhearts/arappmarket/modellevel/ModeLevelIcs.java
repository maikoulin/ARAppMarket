package com.winhearts.arappmarket.modellevel;

import android.content.Context;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.LogInfos;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 数据层-ics-
 *
 * @author liw
 */
public class ModeLevelIcs {
    private final static String TAG = "ModeLevelIcs";
    private final static boolean isDebug = true;

    static public void logReport(final Context mContext, final LogInfos logInfos, final ModeUser<String> user) {
        // 预置默认值
        final String urlStr = Pref.getString(Pref.LOG_REPORT_URL, mContext, Util.getVmsUrl(mContext, ModeUrl.UPLOAD_LOG));
        Type type = new TypeToken<String>() {
        }.getType();
        final Map<String, String> params = RequestUtil.getRequestParam("logInfos", new Gson().toJson(logInfos));
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(urlStr, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                LogDebugUtil.d(isDebug, TAG,
                        urlStr + params.toString() + (data == null ? null : data));
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
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

}
