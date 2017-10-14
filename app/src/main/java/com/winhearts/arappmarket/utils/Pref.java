package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 主要是存储vms的对应的数据，这里把首页的缓冲液存在这了是不大合理的
 */
public class Pref {
    /**
     * Preferences的存储文件名称
     */
    private final static String PREFS_NAME = "pref";

    public static final String UNINSTALL_APP_LIST = "uninstall_app_list";
    public static final String PROJECT_NAME = "project_name";
    public static final String CONFIG_FETCH_CYCLE = "config_fetch_cycle";
    public static final String AMS_URL = "ams_url";
    public static final String ACT_URL = "act_url";
    public static final String UPDATE_TIME = "update_time";
    public static final String LAYOUT_VERSION = "layout_version";
    public static final String LAYOUT_STRING = "layout_string";
    public static final String LOG_REPORT_CYCLE = "log_report_cycle";
    public static final String LOG_REPORT_URL = "log_report_url";
    public static final String CONFIG_CV = "cv";
    public static final String PACKAGE_LIST = "packageList";
    public static final String APP_REPORT_CYCLE = "appinfo_report_cycle";
    public static final String SHOW_OTHER_APP_SWITCH = "show_other_app_switch";
    public static final String CLOUD_GAME_URL = "cloud_game_url";
    public static final String AD_CYCLE = "ad_cycle";
    public static final String LAYOUT_CODE = "layout_code";
    public static final String PHONEHELPER_DOWNLOAD_URL = "phonehelper_download_url";
    public static final String IS_EXIT_DIALOG_SHOW = "is_exit_dialog_show";
    public static final String QQ_GROUP = "qq_group";
    public static final String HASH_TIME = "hashTime";
    public static final String SHOW_INSTALL_HINT = "show_install_hint";
    public static final String LOCK_PASS = "lock_pass";
    public static final String LOG_LEVEl = "logLevel";
    public static final String NEW_USER_RECOMMEND = "new_user_recommend";
    public static final String SHOW_RECOMMEND = "show_recommend";
    public static final String SHOW_MESSAGE_CENTER = "show_message_center";
    public static final String LOTTERY_URL = "lottery_url";
    public static final String SAVE_LAYOUT_COUNT = "save_layout_count";


    private static SharedPreferences getSettings(final Context contex) {

        return contex.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }

    public static String getString(final String key, final Context context, final String defaultValue) {
        return getSettings(context).getString(key, defaultValue);
    }

    public static boolean getBoolean(final String key, final Context context, final boolean defaultValue) {
        return getSettings(context).getBoolean(key, defaultValue);
    }

    public static int getInt(final String key, final Context context, final int defaultValue) {
        return getSettings(context).getInt(key, defaultValue);
    }

    public static long getLong(final String key, final Context context, final long defaultValue) {
        return getSettings(context).getLong(key, defaultValue);
    }

    public static boolean saveString(final String key, final String value, final Context context) {
        final SharedPreferences.Editor settingsEditor = getSettings(context).edit();
        settingsEditor.putString(key, value);
        return settingsEditor.commit();
    }

    public static boolean saveBoolean(final String key, final boolean value, final Context context) {
        final SharedPreferences.Editor settingsEditor = getSettings(context).edit();
        settingsEditor.putBoolean(key, value);
        return settingsEditor.commit();
    }

    public static boolean saveInt(final String key, final int value, final Context context) {
        final SharedPreferences.Editor settingsEditor = getSettings(context).edit();
        settingsEditor.putInt(key, value);
        return settingsEditor.commit();
    }

    public static boolean saveLong(final String key, final long value, final Context context) {
        final SharedPreferences.Editor settingsEditor = getSettings(context).edit();
        settingsEditor.putLong(key, value);
        return settingsEditor.commit();
    }

    public static boolean removeString(final String key, final Context context) {
        final SharedPreferences.Editor settingsEditor = getSettings(context).edit();
        settingsEditor.remove(key);
        return settingsEditor.commit();
    }
}
