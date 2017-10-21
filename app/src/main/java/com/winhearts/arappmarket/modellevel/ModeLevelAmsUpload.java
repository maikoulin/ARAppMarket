package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.AppInfo;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.UploadClick;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * AMS请求，信息上报类
 * Created by lmh on 2015/7/23.
 */
public class ModeLevelAmsUpload {

    private static String TAG = "ModeLevelAmsUpload";

    /**
     * 上传下载路径
     *
     * @param context
     * @param downloadPath
     * @param user
     */
    public static void queryUploadDownPath(final Context context, DownloadPath downloadPath, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.UPLOAD_DOWNLOAD_PATH);
        long currentTime = Util.getAmendCurrentTime(url);// 获取修正后的当前时间
        String macAddress = MacUtil.getMacAddress();
        String accountWsId = ConstInfo.accountWsId;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("wsId", accountWsId);
        map.put("mac", macAddress);
        String verNo = Pref.getString(Pref.LAYOUT_VERSION, context, "");
        map.put("verNo", verNo);
        if (downloadPath == null) {
            return;
        }
        map.put("layoutId", downloadPath.getLayoutId());
        map.put("menuId", downloadPath.getMenuId());
        String subMenuId = downloadPath.getSubMenuId();
        if (subMenuId != null) {
            map.put("subMenuId", subMenuId);
        }
        String moduleId = downloadPath.getModuleId();
        if (moduleId != null) {
            map.put("moduleId", moduleId);
        }
        String modulePath = downloadPath.getModulePath();
        if (modulePath != null) {
            map.put("modulePath", modulePath);
        }

        map.put("time", currentTime + "");
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
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
     * 上传点击效果
     *
     * @param context
     * @param list
     */
    public static void queryUploadClick(final Context context, List<UploadClick> list,
                                        String layoutId, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.UPLOAD_CLICK);
        String accountWsId = ConstInfo.accountWsId;
        HashMap map = new HashMap();
        map.put("mac", MacUtil.getMacAddress());
        map.put("wsId", accountWsId);
        map.put("layoutId", layoutId);
        String verNo = Pref.getString(Pref.LAYOUT_VERSION, context, "");
        map.put("verNo", verNo);
        map.put("data", list);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, "--------code----" + errorCode + "--e----" + errorMessage.getMessage());
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
     * 上传首次打开
     *
     * @param mContext
     * @param packageName
     * @param key
     */
    public static void updateNoOpenApp(Context mContext, String packageName, String key) {
        String noOpenApp = PrefNormalUtils.getString(mContext, key, null);
        if (!TextUtils.isEmpty(noOpenApp) && noOpenApp.contains(packageName)) {
            //上传首次打开成功
            if (key.equals(PrefNormalUtils.NO_OPEN_APP)) {
                uploadOperateData(mContext, packageName, null, "OPEN_SUCCESS", true);
            }
            ArrayList<String> noOpens = new Gson().fromJson(noOpenApp, new TypeToken<ArrayList<String>>() {
            }.getType());
            noOpens.remove(packageName);
            PrefNormalUtils.putString(mContext, key, new Gson().toJson(noOpens));
        }
    }

    /**
     * 上传下载成功和安装成功记录
     *
     * @param context
     * @param packageName
     * @param versionName
     * @param type
     * @param isAddVersion
     */
    public static void uploadOperateData(Context context, String packageName, String versionName, String type, boolean isAddVersion) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("packageName", packageName);
        if (versionName == null && isAddVersion) {
            versionName = ManagerUtil.getPackageInfoName(context, packageName);
        }
        map.put("versionName", versionName);
        map.put("mac", MacUtil.getMacAddress());
        map.put("wsId", ConstInfo.accountWsId);
        map.put("type", type);
        ModeLevelAmsUpload.uploadOperateData(context, map, null);
    }

    /**
     * 上传下载成功和安装成功记录
     *
     * @param mContext
     * @param map
     * @param user
     */
    public static void uploadOperateData(Context mContext, Map<String, String> map, final ModeUserErrorCode<String> user) {
        final String url = Util.getUrl(mContext, ModeUrl.UPLOAD_OPERATE_DATA);
        final Map<String, String> mRequestParams = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, mRequestParams, false, new UIDataListener<String>() {
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

    /**
     * 上报已安装应用列表
     */

    public static void uploadAppList(final Context context, final ModeUser<String> user) {
        List<AppInfo> appInfos = AppManager.getUserApp(context);
        List<String> packageNames = new ArrayList<String>();
        for (AppInfo appInfo : appInfos) {
            packageNames.add(appInfo.getPackageName());
        }
        packageNames.add("com.winhearts.arappmarket.activity");

        //----3.6.0新增
        HashSet<String> packageNamesHash = new HashSet<>(packageNames);
        String instsallAppList = PrefNormalUtils.getString(context, PrefNormalUtils.INSTALL_APP_LIST);
        if (!TextUtils.isEmpty(instsallAppList)) {
            //将其转至hashSet,防止意外情况：arraylist里顺序不一致
            HashSet<String> packageNamesLocal = new Gson().fromJson(instsallAppList, HashSet.class);
            if (packageNamesLocal.equals(packageNamesHash)) {
                LoggerUtil.d(TAG, "the install app is the same of local..");
                return;
            }
        }
        PrefNormalUtils.putString(context, PrefNormalUtils.INSTALL_APP_LIST, new Gson().toJson(packageNamesHash));
        //----end---

        final String url = Util.getUrl(context, ModeUrl.UPLOAD_APP_LIST);
        String accountWsId = ConstInfo.accountWsId;
        HashMap map = new HashMap();
        map.put("mac", MacUtil.getMacAddress());
        map.put("wsId", accountWsId);
        map.put("packageNames", packageNames);

        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, "--------code----" + errorCode + "--e----" + errorMessage.getMessage());
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

    public static void uploadUninstallApp(Context context, String packageName, final ModeUser<String> user) {

        String uninstallString = Pref.getString(Pref.UNINSTALL_APP_LIST, context, "");
        if (!TextUtils.isEmpty(uninstallString)) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> list = new Gson().fromJson(uninstallString, type);
            if (list != null && !list.isEmpty() && list.contains(packageName)) {
                return;
            }
        }

        final String url = Util.getUrl(context, ModeUrl.UPLOAD_UNINSTALL_App);
        Map<String, String> map = new HashMap<String, String>();
        String accountWsId = ConstInfo.accountWsId;
        map.put("mac", MacUtil.getMacAddress());
        map.put("wsId", accountWsId);
        map.put("packageName", packageName);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setTimeOut(8000, 2);
        subVolleyResponseHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                LogDebugUtil.d("卸载上报成功", data);
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, "--------code----" + errorCode + "--e----" + errorMessage.getMessage());
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
