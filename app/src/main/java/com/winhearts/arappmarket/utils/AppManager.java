package com.winhearts.arappmarket.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.winhearts.arappmarket.model.AppInfo;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用工具类
 */
public class AppManager {

    private static final String APPMARKETPACKAGENAME = "com.winhearts.arappmarket.activity";
    private static final String APPMANAGERPACKAGENAME = "com.winhearts.appmanager";

    /**
     * 获得所有Application 过滤自己
     *
     * @param context
     * @return
     */
    public static HashMap<String, SoftwareInfo> getAllApplicationInfo(
            Context context) {
        String recentOpenApp = PrefNormalUtils.getString(context, PrefNormalUtils.RECENT_OPEN_LIST, null);
        HashMap<String, SoftwareInfo> mAllApps = new HashMap<>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo app : apps) {
            // 过滤自己应用
            if (!app.activityInfo.packageName.equals(APPMARKETPACKAGENAME)
                    && !app.activityInfo.packageName
                    .equals(APPMANAGERPACKAGENAME)) {
                SoftwareInfo softwareInfo = new SoftwareInfo();
                softwareInfo.setPackageName(app.activityInfo.packageName);
                softwareInfo.setName(app.activityInfo.loadLabel(manager)
                        .toString());
                softwareInfo.setWhite(false);
                softwareInfo.setInstalled(true);
                if (!TextUtils.isEmpty(recentOpenApp)) {
                    int index = recentOpenApp.indexOf(app.activityInfo.packageName);
                    if (index == -1) {
                        index = Integer.MAX_VALUE;
                    }
                    softwareInfo.setInstallTime(index);
                }
                try {
                    softwareInfo
                            .setVersionCode(String.valueOf(manager
                                    .getPackageInfo(
                                            app.activityInfo.packageName, 0).versionCode));
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // SoftwareInfo.setDrawable(app.loadIcon(manager));
                mAllApps.put(app.activityInfo.packageName, softwareInfo);
            }

        }
        return mAllApps;
    }

    /**
     * 获得所有Application 包括自己
     *
     * @param context
     * @return
     */
    public static HashMap<String, SoftwareInfo> getAllApplicationInfoEx(
            Context context) {
        HashMap<String, SoftwareInfo> allApps = new HashMap<String, SoftwareInfo>();

        //返回给定条件的所有ResolveInfo对象(本质上是Activity)，集合对象
        //查询Android系统的所有具备ACTION_MAIN和CATEGORY_LAUNCHE的Intent的应用程序
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo app : apps) {
            SoftwareInfo SoftwareInfo = new SoftwareInfo();
            SoftwareInfo.setPackageName(app.activityInfo.packageName);
            SoftwareInfo.setName(app.activityInfo.loadLabel(manager)
                    .toString());
            SoftwareInfo.setInstalled(true);
            try {
                SoftwareInfo
                        .setVersionCode(String.valueOf(manager
                                .getPackageInfo(
                                        app.activityInfo.packageName, 0).versionCode));
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // SoftwareInfo.setDrawable(app.loadIcon(manager));
            allApps.put(app.activityInfo.packageName, SoftwareInfo);


        }
        return allApps;
    }

    public static Drawable getAppIcon(Context mContext, String packageName)
            throws NameNotFoundException {
        PackageManager manager = mContext.getPackageManager();
        return manager.getApplicationIcon(packageName);
    }

    /**
     * 系统应用
     *
     * @return
     */
    public static HashMap<String, SoftwareInfo> getSystemApp(Context context) {
        HashMap<String, SoftwareInfo> mSystemApps = new HashMap<String, SoftwareInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                SoftwareInfo softwareInfo = new SoftwareInfo();
                softwareInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm).toString());
                softwareInfo.setName(packageInfo.applicationInfo.loadLabel(pm)
                        .toString());
                softwareInfo.setPackageName(packageInfo.applicationInfo.packageName);
                mSystemApps.put(packageInfo.applicationInfo.packageName, softwareInfo);
            }

        }
        return mSystemApps;
    }

    /**
     * 用户应用
     *
     * @return
     */
    public static HashMap<String, SoftwareInfo> getUserApps(Context context) {
        HashMap<String, SoftwareInfo> mUserApps = new HashMap<String, SoftwareInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && (!packageInfo.applicationInfo.packageName
                    .equals(APPMARKETPACKAGENAME))) {
                SoftwareInfo softwareInfo = new SoftwareInfo();
                softwareInfo.setName(packageInfo.applicationInfo.loadLabel(pm)
                        .toString());
                softwareInfo.setPackageName(packageInfo.applicationInfo.packageName);
                softwareInfo.setInstalled(true);
                softwareInfo.setWhite(false);
//                softwareInfo.setInstallTime(AppManager.getInstallTime(context, packageInfo.applicationInfo.packageName));
                try {
                    softwareInfo.setVersionCode(String.valueOf(pm
                            .getPackageInfo(
                                    packageInfo.applicationInfo.packageName, 0).versionCode));
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mUserApps.put(packageInfo.applicationInfo.packageName, softwareInfo);
            }
        }
        return mUserApps;
    }

    /**
     * 用户应用
     *
     * @return
     */
    public static List<AppInfo> getUserApp(Context context) {
        List<AppInfo> mUserApps = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && (!packageInfo.applicationInfo.packageName
                    .equals(APPMARKETPACKAGENAME))) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
                appInfo.setName(packageInfo.applicationInfo.loadLabel(pm)
                        .toString());
                appInfo.setPackageName(packageInfo.applicationInfo.packageName);
                mUserApps.add(appInfo);
            }
        }
        return mUserApps;
    }

    /**
     * 用户应用
     *
     * @return
     */
    public static Map<String, Long> getUserAppPkg(Context context) {
        Map<String, Long> mUserApps = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        long timeMillis = System.currentTimeMillis();
        String packageList = Pref.getString(Pref.PACKAGE_LIST, context, null);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && (!packageInfo.applicationInfo.packageName
                    .equals(APPMARKETPACKAGENAME))) {
                //   只保留平台运用
                if (!TextUtils.isEmpty(packageList)) {
                    if (packageList.contains(packageInfo.applicationInfo.packageName)) {
                        mUserApps.put(packageInfo.applicationInfo.packageName, timeMillis);
                    }
                } else {
                    mUserApps.put(packageInfo.applicationInfo.packageName, timeMillis);
                }
            }
        }
        return mUserApps;
    }

    public static SoftwareInfo getAppMessage(Context context, String packageName) {
        if (context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return null;
        } else {
            String recentOpenApp = PrefNormalUtils.getString(context, PrefNormalUtils.RECENT_OPEN_LIST, null);
            SoftwareInfo softwareInfo = new SoftwareInfo();
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(packageName, 0);
                softwareInfo.setName(packageInfo.applicationInfo.loadLabel(context.getPackageManager())
                        .toString());
                softwareInfo.setPackageName(packageName);
                softwareInfo.setInstalled(true);
                softwareInfo.setWhite(false);
                if (!TextUtils.isEmpty(recentOpenApp)) {
                    int index = recentOpenApp.indexOf(packageName);
                    if (index == -1) {
                        index = Integer.MAX_VALUE;
                    }
                    softwareInfo.setInstallTime(index);
                }
                softwareInfo.setVersionCode(String.valueOf(packageInfo.versionCode));
                return softwareInfo;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static int getVersionCode(Context context, String packageName) {
        if (context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return 0;
        } else {
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(packageName, 0);
                return packageInfo.versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    //获取应用名称
    public static String getAppName(Context context, String packageName) {
        if (context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return "null";
        } else {
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(packageName, 0);
                return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return "null";
        }
    }

    public static long getInstallTime(Context context, String packageName) {
        if (context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return 0;
        } else {
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(packageName, 0);
                return packageInfo.lastUpdateTime;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager
                .getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getPackageName();
        }
        return topActivityClassName;
    }

    public static boolean isTopActivity(Context context) {
        return APPMARKETPACKAGENAME.equals(getTopActivityName(context));
    }


}
