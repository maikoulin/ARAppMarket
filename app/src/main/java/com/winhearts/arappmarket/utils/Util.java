package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import com.winhearts.arappmarket.network.VolleyResponseHandler;
import com.winhearts.arappmarket.constant.ConstVersionUrl;
import com.winhearts.arappmarket.model.Element;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.Screen;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *  常用方法
 */
public class Util {

    public static final String APK_NAME = "activity.apk";

    //vms也是http
    public static String getVmsUrl(Context context, String stringUrl) {
        return ConstVersionUrl.VMS + stringUrl;
    }

    /**
     * AMS 的 URL
     *
     * @param context
     * @return
     */
    public static String getAMSUrl(Context context) {
        String amsUrl = Pref.getString(Pref.AMS_URL, context, null);
        if (TextUtils.isEmpty(amsUrl)) {
            return ConstVersionUrl.AMS;
        } else {
            return amsUrl;
        }
    }

    /**
     * AMS 的 URL
     *
     * @param context
     * @param stringUrl
     * @return
     */
    public static String getUrl(Context context, String stringUrl) {
        String amsUrl = Pref.getString(Pref.AMS_URL, context, "");
        if (amsUrl.equals("")) {

            return ConstVersionUrl.AMS + stringUrl;
        } else {

            return amsUrl + stringUrl;
        }
    }

    /**
     * Act 的 URL
     *
     * @param context
     * @param stringUrl
     * @return
     */
    public static String getActUrl(Context context, String stringUrl) {
        String actUrl = Pref.getString(Pref.ACT_URL, context, "");
        if (actUrl.equals("")) {
            return ConstVersionUrl.ACT + stringUrl;
        } else {
            return actUrl + stringUrl;

        }
    }

    public static long getAmendCurrentTime(String url) {
        String host = getHost(url);
        if (host != null) {
            Long diffTime = VolleyResponseHandler.diffTimeMap.get(host);
            if (diffTime == null) {
                return SystemClock.elapsedRealtime() / 1000;
            } else {
                return SystemClock.elapsedRealtime() / 1000 + diffTime;
            }
        } else {
            return SystemClock.elapsedRealtime() / 1000;
        }
    }

    public static long getAmendCurrentTimeMs(String url) {
        String host = getHost(url);
        if (host != null) {
            Long diffTime = VolleyResponseHandler.diffTimeMap.get(host);
            if (diffTime == null) {
                return SystemClock.elapsedRealtime();
            } else {
                return SystemClock.elapsedRealtime() + diffTime * 1000;
            }
        } else {
            return SystemClock.elapsedRealtime();
        }
    }

    public static String getHost(String url) {
        try {
            URI uri = new URI(url);

            return uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return String.valueOf(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String toTimeString(int milliseconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm:ss", Locale.UK);
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(milliseconds);
    }


    public static void installApk(Context context, String apkName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), apkName)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    public static String getMD5(String val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] b = md5.digest();

        int i;

        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }

        return buf.toString();
    }

    public static int getVersionSdk() {
        return Build.VERSION.SDK_INT;
    }

    public static String getBox() {
        int versionSdk = getVersionSdk();
        if (versionSdk == 15) {
            return "CLOUD";
        } else if (versionSdk == 19) {
            return "FUMULE";
        }
        return String.valueOf(versionSdk);
    }


    public static void clearEmptyAndSortScreen(Layout layout) {
        if (layout != null) {
            List<MenuItem> menuItems = layout.getMenuInfos();
            if (menuItems != null) {
                for (int i = 0; i < menuItems.size(); i++) {
                    MenuItem menuItem = menuItems.get(i);
                    clearEmptyAndSortScreen(menuItem);
                }
            }
        }
    }

    public static void clearEmptyAndSortScreen(MenuItem menuItem) {
        if (menuItem.getHasChild() == 1) {
            List<MenuItem> childMenuItems = menuItem.getChild();
            if (childMenuItems != null) {
                for (int j = 0; j < childMenuItems.size(); j++) {
                    MenuItem childItem = childMenuItems.get(j);
                    clearEmptyAndSortScreen(childItem);
                }
            }
        } else {
            if (menuItem != null) {
                String dataType = menuItem.getMenuDataType();
                if (dataType.equals("SELF_DEFINE")) {
                    List<Screen> screens = menuItem.getScreens();
                    if (screens != null) {
                        Collections.sort(screens);
                        int size = screens.size();
                        for (int k = size - 1; k >= 0; k--) {
                            Screen screen = screens.get(k);
                            ArrayList<Element> elements = screen.getElementResInfoList();
                            if (elements != null) {
                                if (elements.size() == 0) {
                                    //		 Log.i("tag","size is zero");
                                    screens.remove(k);
                                } else {
                                    //			 Log.i("tag","size is not zero");
                                }
                            } else {
                                //		 Log.i("tag","elements is null");
                            }
                        }
                    }
                }
            }
        }
    }

}
