package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.os.Build;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstVersionUrl;
import com.winhearts.arappmarket.model.AppUpdate;
import com.winhearts.arappmarket.model.ClientInfo;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 数据层-vms-
 *
 * @author liw
 */
public class ModeLevelVms {
    private static String TAG = "ModeLevelVms";

    /**
     * 获取配置文件
     *
     * @param context
     * @param user
     */
    static public void queryConfig(final Context context, final ModeUser<ConfigInfo> user) {
        final String url = Util.getVmsUrl(context, ModeUrl.GET_CONFIG_LIST);
        final ClientInfo clientInfo = new ClientInfo();
        clientInfo.setCv(Pref.getString(Pref.CONFIG_CV, context, "0"));
        clientInfo.setVersionCode(Util.getVersionCode(context));// 版本号
        clientInfo.setPackageName(context.getPackageName());// 包名
        clientInfo.setMac(MacUtil.getMacAddress());
        clientInfo.setBoxCode(Util.getBox());
        clientInfo.setProject(Pref.getString(Pref.PROJECT_NAME, context, ConstVersionUrl.PROJECT));
        clientInfo.setOs(Build.VERSION.RELEASE);
        clientInfo.setDevice(Build.DEVICE);

        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(clientInfo));
        Type type = new TypeToken<ConfigInfo>() {
        }.getType();
        SubVolleyResponseHandler<ConfigInfo> subVolleyResponseHandler = new SubVolleyResponseHandler<ConfigInfo>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<ConfigInfo>() {
            @Override
            public void onDataChanged(ConfigInfo data) {
                LogDebugUtil.d(
                        TAG,
                        "success" + url + params.toString()
                                + (data == null ? null : data.toString()));
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

    /**
     * 不用静态，请勿改动。static会引起多线程回调出现问题
     * 强制获取配置文件保存
     *
     * @param context
     * @param user
     */
    public static void queryConfigForce(final Context context, int maxNumRetries, Object tag, final ModeUserErrorCode<ConfigInfo> user) {
        final String url = Util.getVmsUrl(context, ModeUrl.GET_CONFIG_LIST);
        final ClientInfo clientInfo = new ClientInfo();
        //版本号为空就是强制获取
//        clientInfo.setCv(Pref.getString(Pref.CONFIG_CV, context, "0"));
        clientInfo.setVersionCode(Util.getVersionCode(context));// 版本号
        clientInfo.setPackageName(context.getPackageName());// 包名
        clientInfo.setMac(MacUtil.getMacAddress());
        clientInfo.setBoxCode(Util.getBox());
        clientInfo.setProject(Pref.getString(Pref.PROJECT_NAME, context, ConstVersionUrl.PROJECT));
        clientInfo.setOs(Build.VERSION.RELEASE);
        clientInfo.setDevice(Build.DEVICE);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(clientInfo));
        Type type = new TypeToken<ConfigInfo>() {
        }.getType();
        SubVolleyResponseHandler<ConfigInfo> subVolleyResponseHandler = new SubVolleyResponseHandler<ConfigInfo>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, maxNumRetries);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<ConfigInfo>() {
            @Override
            public void onDataChanged(ConfigInfo data) {
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

    static public void appUpdate(final Context mContext,
                                 final ModeUser<AppUpdate> user) {

        final String versionCheckUrl = Util.getVmsUrl(mContext, ModeUrl.CHECK_VERSION);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setVersionCode(Util.getVersionCode(mContext));// 版本号
        clientInfo.setPackageName(mContext.getPackageName());// 包名
        clientInfo.setMac(MacUtil.getMacAddress());
        clientInfo.setBoxCode(Util.getBox());
        clientInfo.setProject(Pref.getString(Pref.PROJECT_NAME, mContext, ConstVersionUrl.PROJECT));
        clientInfo.setOs(Build.VERSION.RELEASE);
        clientInfo.setDevice(Build.DEVICE);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(clientInfo));
        Type type = new TypeToken<AppUpdate>() {
        }.getType();
        SubVolleyResponseHandler<AppUpdate> subVolleyResponseHandler = new SubVolleyResponseHandler<AppUpdate>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(versionCheckUrl, params, false, new UIDataListener<AppUpdate>() {
            @Override
            public void onDataChanged(AppUpdate data) {
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
