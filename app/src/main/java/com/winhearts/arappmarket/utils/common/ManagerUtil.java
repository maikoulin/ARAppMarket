package com.winhearts.arappmarket.utils.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.winhearts.arappmarket.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.event.OpenAPkEvent;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对package，activity，service的一些判断
 *
 * @author liw
 */
public class ManagerUtil {
    static private final String TAG = "ManagerUtil";

    /**
     * 启动apk -- 未测试
     *
     * @param context
     * @param packageName
     */
    public static void startApk(Context context, String packageName) {
        ModeLevelAmsUpload.updateNoOpenApp(context, packageName, PrefNormalUtils.ALL_NO_OPEN_APP);
        ModeLevelAmsUpload.updateNoOpenApp(context, packageName, PrefNormalUtils.NO_OPEN_APP);
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            recordOpenApps(context, packageName);
            RxBus.getDefault().post(new OpenAPkEvent(packageName));
        } catch (Exception e) {
            LoggerUtil.e(context.getResources().getString(R.string.app_open_error), packageName);
            ToastUtils.show(context, "应用打开异常！");
        }
    }

    public static void recordOpenApps(Context context, String packageName) {
        String recentOpenApp = PrefNormalUtils.getString(context, PrefNormalUtils.RECENT_OPEN_LIST, null);
        ArrayList<String> Opens;
        if (TextUtils.isEmpty(recentOpenApp)) {
            Opens = new ArrayList<>();
            Opens.add(packageName);
        } else {
            Opens = new Gson().fromJson(recentOpenApp, new TypeToken<ArrayList<String>>() {
            }.getType());
            if (recentOpenApp.contains(packageName)) {
                Opens.remove(packageName);
                Opens.add(0, packageName);
            } else {
                Opens.add(0, packageName);
            }
        }
        PrefNormalUtils.putString(context, PrefNormalUtils.RECENT_OPEN_LIST, new Gson().toJson(Opens));

    }

    public static void deleteOpenApp(Context context, String packageName) {
        String recentOpenApp = PrefNormalUtils.getString(context, PrefNormalUtils.RECENT_OPEN_LIST, null);
        if (!TextUtils.isEmpty(recentOpenApp) && recentOpenApp.contains(packageName)) {
            ArrayList<String> opens = new Gson().fromJson(recentOpenApp, new TypeToken<ArrayList<String>>() {
            }.getType());
            opens.remove(packageName);
            PrefNormalUtils.putString(context, PrefNormalUtils.RECENT_OPEN_LIST, new Gson().toJson(opens));
        }

    }

    /**
     * 指定service的运行状态
     *
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (serviceName == null) {
            return false;
        }

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取前台进程的应用包名(一般都不止一个，目前知道的顺序不可预知)
     *
     * @param context
     * @return
     */
    public static List<String> getCurrentAppPackageName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<String> list = new ArrayList<String>();

        try {
            List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {

                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    list.add(appProcessInfo.processName);
                    // LogDebugUtil.d("lee", new Date().toLocaleString() +
                    // "  foreground  " + appProcessInfo.processName);

                }
            }
        } catch (Exception e) {

            return null;
        }
        return list;

    }

    /**
     * 当前运行的包名 deprecated in api 21
     *
     * @param context
     * @return
     */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getPackageName();
        } else {
            return null;
        }
        return topActivityClassName;
    }

    /**
     * 本次运行的软件列表
     *
     * @param context
     * @return
     */
    public static List<String> getRecentTasks(Context context) {
        List<String> recentPackageName = new ArrayList<>();
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(30, ActivityManager.RECENT_WITH_EXCLUDED);
        if (!ContainerUtil.isEmpty(recentTasks)) {
            for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTasks) {
                recentPackageName.add(recentTaskInfo.baseIntent.getComponent().getPackageName());
            }
        }
        return recentPackageName;
    }

    /**
     * 检查包是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    static public boolean checkApplication(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            // ApplicationInfo info =
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断 activity 是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    static public boolean checkActivity2(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0) != null;

    }

    /**
     * 判断 activity 是否存在 intent.setComponent(comp) 的方式，测不出来是否含有该activity都是true
     *
     * @param context
     * @param intent
     * @return
     */
    static public boolean checkActivity(Context context, Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * 判断 activity 是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    static public boolean checkActivity3(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        return list.size() != 0;

    }

    /**
     * 是否是系统软件
     *
     * @return
     */
    static public boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * 是否是系统软件
     *
     * @param pInfo
     * @return
     */
    static public boolean isSystemApp(ApplicationInfo pInfo) {
        return ((pInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * 是否是系统软件的更新软件
     *
     * @param pInfo
     * @return
     */
    static public boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    /**
     * 是否是系统软件的更新软件
     *
     * @param pInfo
     * @return
     */
    static public boolean isSystemUpdateApp(ApplicationInfo pInfo) {
        return ((pInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    /**
     * 是否是系统软件或者系统更新上来的软件
     *
     * @param pInfo
     * @return
     */
    static public boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    /**
     * 获取指定包名的图标 (在activty里直接获取可以，写成api没成功)
     *
     * @param activity   必须要是activity的Context 才能获取的到
     * @param pakageName
     * @return
     */
    @Deprecated
    public static Drawable getIcon(Activity activity, String pakageName) {
        PackageInfo info = null;
        try {
            PackageManager pm = activity.getPackageManager();
            info = pm.getPackageInfo(pakageName, PackageManager.GET_META_DATA);

            Drawable iconDrawable = info.applicationInfo.loadIcon(pm);
            return iconDrawable;

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogDebugUtil.e("lee2", e.toString());
            return null;
        }
    }

    /**
     * 根据包名获取版本名
     *
     * @param context
     * @param pakageName
     * @return
     */
    public static String getPackageInfoName(Context context, String pakageName) {
        PackageInfo info = null;
        try {
            PackageManager pm = context.getPackageManager();
            info = pm.getPackageInfo(pakageName, PackageManager.GET_META_DATA);

            return info.versionName;

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 根据包名获取版本号
     *
     * @param context
     * @param pakageName
     * @return
     */
    public static int getPackageInfoVersionInt(Context context, String pakageName) {
        PackageInfo info = null;
        try {
            PackageManager pm = context.getPackageManager();
            info = pm.getPackageInfo(pakageName, 0);
            return info.versionCode;

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 打开一个app
     */
    public static boolean openAPP(Context context, String appPackageName) {
        try {

            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPackageAppExist(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent filter = new Intent(Intent.ACTION_MAIN);
        filter.setPackage(packageName);
        filter.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(filter,
                PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /**
     * 打开某个应用的非主界面
     */
    public static boolean startDataActivity(Context context, String packName,
                                            String schemeUrl) {
        try {
            Uri uri = Uri.parse(schemeUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            //ToastUtils.show(context,"该应用的" + schemeUrl + "地址无法打开，请检查地址是否正确！" );
            LoggerUtil.w(TAG, "" + Log.getStackTraceString(e));
            return false;
        } catch (NullPointerException e) {
            LoggerUtil.w(TAG, "" + Log.getStackTraceString(e));
            return false;
            //ToastUtils.show(context, "该应用的打开地址为空，请检查地址是否正确！");
        }
    }

    public static RunningTaskInfo getTopTask(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    public static boolean isTopActivity(Context context,
                                        String packageName, String activityName) {
        RunningTaskInfo topTask = getTopTask(context);
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;
            if (topActivity.getPackageName().equals(packageName) &&
                    topActivity.getClassName().contains(activityName)) {
                return true;
            }
        }

        return false;
    }
}
