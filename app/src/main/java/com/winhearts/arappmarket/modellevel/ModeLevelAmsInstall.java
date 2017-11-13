package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.text.TextUtils;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.DownloadInfo;
import com.winhearts.arappmarket.model.InstallBatsEntity;
import com.winhearts.arappmarket.model.InstallPresEntity;
import com.winhearts.arappmarket.model.InstallUrlEntity;
import com.winhearts.arappmarket.model.QuerySoftwareDownload;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AMS请求，与应用安装等相关
 * Created by lmh on 2015/7/23.
 */
public class ModeLevelAmsInstall {
    private final static String TAG = "ModeLevelAmsInstall";

    /**
     * 获取下载url
     *
     * @param mContext
     * @param packageName 包名
     */
    public static void querySoftwareDownload(final Context mContext, String packageName,
                                             final ModeUserErrorCode<DownloadInfo> user) {
        final String url = Util.getUrl(mContext, ModeUrl.QUERY_DOWNLOAD);
        QuerySoftwareDownload mQuerySoftwareDownload = new QuerySoftwareDownload();
        mQuerySoftwareDownload.setPackageName(packageName);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(mQuerySoftwareDownload));
        Type type = new TypeToken<DownloadInfo>() {
        }.getType();
        SubVolleyResponseHandler<DownloadInfo> subVolleyResponseHandler = new SubVolleyResponseHandler<DownloadInfo>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<DownloadInfo>() {
            @Override
            public void onDataChanged(DownloadInfo data) {

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

    /**
     * 获取预安装列表
     *
     * @param context
     */
    public static void queryInstallPres(final Context context, String version, final ModeUser<InstallPresEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.INSTALL_PRE);
        String macAddress = MacUtil.getMacAddress();
        String accountwinId = ConstInfo.accountWinId;
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("mac", macAddress);
        map.put("winId", accountwinId);
        if (!TextUtils.isEmpty(version)) {
            map.put("version", version);
        }
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<InstallPresEntity>() {
        }.getType();
        SubVolleyResponseHandler<InstallPresEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<InstallPresEntity>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<InstallPresEntity>() {
            @Override
            public void onDataChanged(InstallPresEntity data) {
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
     * 批量获取url
     *
     * @param context
     * @param list
     * @param user
     */
    public static void queryInstallUrls(final Context context, List<String> list, final ModeUser<InstallUrlEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.INSTALL_URLS);
        String macAddress = MacUtil.getMacAddress();
        String accountwinId = ConstInfo.accountWinId;
        final InstallBatsEntity entity = new InstallBatsEntity();
        entity.setMac(macAddress);
        entity.setwinId(accountwinId);
        entity.setPackageNames(list);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(entity));
        Type type = new TypeToken<InstallUrlEntity>() {
        }.getType();
        SubVolleyResponseHandler<InstallUrlEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<InstallUrlEntity>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<InstallUrlEntity>() {
            @Override
            public void onDataChanged(InstallUrlEntity data) {

                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.e(TAG, url + " " + errorMessage.toString());
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
     * 检测是否可以卸载
     *
     * @param context
     * @param packageName
     */
    public static void queryAbleUninstall(final Context context, String packageName, final ModeUser<String> user) {
        String url = Util.getUrl(context, ModeUrl.INSTALL_CHECKABLE_UNINSTALL);
        String macAddress = MacUtil.getMacAddress();
        String accountwinId = ConstInfo.accountWinId;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("packageName", packageName);
        map.put("mac", macAddress);
        map.put("winId", accountwinId);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        SubVolleyResponseHandler<HashMap<String, String>> subVolleyResponseHandler = new SubVolleyResponseHandler<HashMap<String, String>>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<HashMap<String, String>>() {
            @Override
            public void onDataChanged(HashMap<String, String> data) {
                String uninstall = data.get("uninstall");
                if (user != null) {
                    user.onJsonSuccess(uninstall);
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

    static public void checkUpdate(final Context mContext, boolean isAsync, List<SoftwareInfo> list, final String autoUpgrade, final ModeUser<List<SoftwareInfo>> user) {

        final String url = Util.getUrl(mContext, ModeUrl.INSTALL_UPDATE);
        Softwares softWares = new Softwares();
        softWares.setSoftwares(list);
        softWares.setAutoUpgrade(autoUpgrade);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(softWares));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<Softwares>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.isAsync(isAsync);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
                if (data != null) {
                    List<SoftwareInfo> softWares = data
                            .getSoftwares();
                    if (data.getAutoUpgrade().equals("0")) {
                        PrefNormalUtils.putString(PrefNormalUtils.UPDATE_LIST, new Gson().toJson(softWares));
                    }
                    if (user != null) {
                        user.onJsonSuccess(softWares);
                    }
                } else {
                    if (autoUpgrade.equals("0")) {
                        PrefNormalUtils.putString(PrefNormalUtils.UPDATE_LIST, "");
                    }
                    if (user != null) {
                        user.onJsonSuccess(null);
                    }
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
