package com.winhearts.arappmarket.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.io.File;


/**
 * UI相关的一些操作
 *
 * @author huyf
 */
public class UIHelper {

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        ToastUtils.show(cont, msg);
    }

    public static void ToastMessage(Context cont, int msg) {
        ToastUtils.show(cont, msg);
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        ToastUtils.show(cont, msg, time);
    }

    /**
     * 点击返回监听事件
     *
     * @param activity
     * @return
     */
    public static View.OnClickListener finish(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        };
    }

    /**
     * 用系统浏览器打开指定url
     *
     * @param context
     */
    public static void showUrlContentBySystem(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 根据上下文获取版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        // 获取PackageManager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = null;
        if (packInfo != null) {
            version = packInfo.versionName;
        }
        return version;
    }

    public static void installApk(Context context, String apkName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), apkName)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void installApkTmp(Context context, String apkName) {
        StorageUtils.getFilesAuthority(apkName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LogDebugUtil.d("PackageUtils", VpnStoreApplication.getApp().getFilesDir().getPath() + "/" + apkName);
        intent.setDataAndType(Uri.parse("file://" + VpnStoreApplication.getApp().getFilesDir().getPath() + "/" + apkName),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
