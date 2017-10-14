package com.winhearts.arappmarket.utils.cust;

import android.text.TextUtils;
import android.util.Log;

import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.ShellUtils;
import com.winhearts.arappmarket.utils.common.ShellUtils.CommandResult;

import java.lang.reflect.Method;

/**
 * 获取系统属性
 *
 * @author liw
 */
public class CaCountUtil {
    private static String TAG = "cacard";

    /**
     * 获取ca卡账号，若果没有ca卡返回null
     *
     * @return
     */
    public static String getCaNum2Test() {

        String uid = System.getProperty("persist.sys.mmcp.smarcardid", "none");
        String uid2 = System.getProperty("persist.sys.mmcp.smarcard", "none");
        String ms = System.getProperty("persist.sys.profiler_ms", "none");

        LogDebugUtil.d(TAG, "--ms--" + ms);
        LogDebugUtil.d(TAG, "--smarcard-id--" + uid + "  --" + uid2);
        LoggerUtil.d("caUid", "--smarcard-id--" + uid + "  --" + uid2);

        getCaNum();

        if ("none".equals(uid)) {
            return null;
        } else {
            return uid;
        }

    }

    /**
     * h获取CA卡号
     *
     * @return 卡号
     */
    static public String getCaNum() {
//		String val = getPersist("persist.sys.mmcp.smarcardid");

        String val = getPropertyReflect("persist.sys.mmcp.smarcardid");

        LogDebugUtil.i(TAG, val);
        if (TextUtils.isEmpty(val)) {
            LogDebugUtil.d("获取Ca卡失败", "没有发现Ca卡");
        }
        return val;
    }

    /**
     * 获取区域码
     *
     * @return 区域码
     */
    public static String getAreaCode() {
        String areaCode = getPropertyReflect("persist.sys.logicnum.areacode", " ");
        if (TextUtils.isEmpty(areaCode)) {
            LoggerUtil.d("获取区域码失败", "获取区域码失败");
        }
        return areaCode;
    }

    /**
     * 用命令读取系统属性
     *
     * @param name
     * @return
     */
    static private String getPersist(String name) {
        CommandResult result = ShellUtils.execCommand("getprop | grep " + name, false, true);
        LogDebugUtil.d(TAG, "--------" + result);

        String content = result.successMsg;
        if (!TextUtils.isEmpty(content)) {
            if (content.contains(name)) {
                String s[] = content.split(":");

                if (s != null && s.length > 1) {
                    String content2 = s[1].trim();

                    if (TextUtils.isEmpty(content2) || content2.length() < 3) {
                        return null;
                    }
                    LogDebugUtil.d(TAG, "content2 = " + content2);

                    String info = content2.substring(1, content2.length() - 1);

                    LogDebugUtil.d(TAG, "info = " + info);

                    return info;

                }
            }
        }

        return null;
    }

    /**
     * 用反射得到系统隐藏api 读取系统属性
     *
     * @param key
     * @return
     */
    public static String getPropertyReflect(String key) {

        // reflect call system properties
        Class osSystem = null;
        try {
            osSystem = Class.forName("android.os.SystemProperties");
            Method getDeviceIDMethod = osSystem.getMethod("get",
                    String.class);
            String tv2deviceid = (String) getDeviceIDMethod.invoke(osSystem,
                    key);
            if (tv2deviceid != null && tv2deviceid.length() > 0) {

                Log.d(TAG, key + " = " + tv2deviceid);
                return tv2deviceid;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return "";
    }

    /**
     * 用反射得到系统隐藏api 读取系统属性
     *
     * @param key1 第一次参数
     * @param key2 第二个参数
     * @return
     */
    private static String getPropertyReflect(String key1, String key2) {
        // reflect call system properties
        Class osSystem;
        try {
            osSystem = Class.forName("android.os.SystemProperties");
            Method getDeviceIDMethod = osSystem.getMethod("get",
                    String.class, String.class);
            String tv2deviceid = (String) getDeviceIDMethod.invoke(osSystem,
                    key1, key2);
            if (!TextUtils.isEmpty(tv2deviceid)) {
                return tv2deviceid;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return "";
    }

}
