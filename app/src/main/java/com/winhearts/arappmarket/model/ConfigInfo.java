package com.winhearts.arappmarket.model;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.winhearts.arappmarket.utils.Pref;

import java.util.List;

/**
 * vms配置下发信息
 */
public class ConfigInfo {

    private String project_name;// 项目名称
    private String config_fetch_cycle;// 配置获取周期
    private String ams_url;  //ams地
    private String log_report_cycle; //日志上报周期
    private String log_report_url;//日志上报地址
    private String cv; //版本号
    private String appinfo_report_cycle;
    private String ad_cycle; //广告扫描周期
    private List<String> uninstall_app_list;//强制卸载列表
    private String layout_code; //终端标示
    private String phonehelper_download_url; //手机助手二维码下载路径
    private String show_sign_in; //是否显示签到
    private String is_exit_dailog_show; // 退出对话框是否显示
    private String qq_group; //qq群
    private String hashTime;  //散列时间
    private String show_install_hint; //是否显示安装成功弹框
    private String logLevel; //上报日志等级:1 信息类、2告警类 3 错误类 4 严重错误类
    private String new_user_recommend;  //新用户推荐
    private String show_recommend;  //定制推荐开关
    private String save_layout_count; //本地保存的屏幕数量


    public String getShow_recommend() {
        return show_recommend;
    }

    public void setShow_recommand(String show_recommend) {
        this.show_recommend = show_recommend;
    }

    public String getNew_user_recommend() {
        return new_user_recommend;
    }

    public void setNew_user_recommend(String new_user_recommend) {
        this.new_user_recommend = new_user_recommend;
    }

    public String getIs_exit_dailog_show() {
        return is_exit_dailog_show;
    }

    public void setIs_exit_dailog_show(String is_exit_dailog_show) {
        this.is_exit_dailog_show = is_exit_dailog_show;
    }

    /**
     * 是否支持从service启动vpn, 广电的盒子是支持的，其他盒子不支持
     * 所以在心跳中有添加一个广播，可以在activity中接收该广播进行activity的开启sdk里的vpn
     */

    public String getConfig_fetch_cycle() {
        return config_fetch_cycle;
    }


    private String show_other_app_switch;

    private String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    private String getAms_url() {
        if (TextUtils.isEmpty(ams_url)) {
            return null;
        } else {
            if (ams_url.startsWith("http://") || ams_url.startsWith("https://")) {
                return ams_url;
            }
            return "https://" + ams_url;
        }
    }

    public void setAms_url(String ams_url) {
        if (ams_url.startsWith("http://") || ams_url.startsWith("https://")) {
            this.ams_url = ams_url;
        } else {
            this.ams_url = "https://" + ams_url;
        }
    }

    public String getLog_report_url() {
        return log_report_url;
    }

    public void setLog_report_url(String log_report_url) {
        this.log_report_url = log_report_url;
    }

    public String getLog_report_cycle() {
        return log_report_cycle;
    }

    public void setLog_report_cycle(String log_report_cycle) {
        this.log_report_cycle = log_report_cycle;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getPhonehelper_download_url() {
        return phonehelper_download_url;
    }

    public void setPhonehelper_download_url(String phonehelper_download_url) {
        this.phonehelper_download_url = phonehelper_download_url;
    }

    public String getShow_install_hint() {
        return show_install_hint;
    }

    public void setShow_install_hint(String show_install_hint) {
        this.show_install_hint = show_install_hint;
    }

    public String getSave_layout_count() {
        return save_layout_count;
    }

    public void setSave_layout_count(String save_layout_count) {
        this.save_layout_count = save_layout_count;
    }

    public static void savaConfig(Context mContext, ConfigInfo config) {

        if (config == null) {
            return;
        }
        saveOrRemove(Pref.CONFIG_FETCH_CYCLE, config.getConfig_fetch_cycle(), mContext);
        saveOrRemove(Pref.PROJECT_NAME, config.getProject_name(), mContext);
        saveOrRemove(Pref.AMS_URL, config.getAms_url(), mContext);
        saveOrRemove(Pref.PHONEHELPER_DOWNLOAD_URL, config.getPhonehelper_download_url(), mContext);
        saveOrRemove(Pref.LOG_REPORT_URL, config.getLog_report_url(), mContext);
        saveOrRemove(Pref.LOG_REPORT_CYCLE, config.getLog_report_cycle(), mContext);
        saveOrRemove(Pref.CONFIG_CV, config.getCv(), mContext);
        saveOrRemove(Pref.APP_REPORT_CYCLE, config.getAppinfo_report_cycle(), mContext);
        saveOrRemove(Pref.SHOW_OTHER_APP_SWITCH, config.getShow_other_app_switch(), mContext);
        saveOrRemove(Pref.LAYOUT_CODE, config.getLayout_code(), mContext);
        saveOrRemove(Pref.UNINSTALL_APP_LIST, config.getUninstall_app_list(), mContext);
        saveOrRemove(Pref.IS_EXIT_DIALOG_SHOW, config.getIs_exit_dailog_show(), mContext);
        saveOrRemove(Pref.QQ_GROUP, config.getQq_group(), mContext);
        saveOrRemove(Pref.HASH_TIME, config.getHashTime(), mContext);
        saveOrRemove(Pref.SHOW_INSTALL_HINT, config.getShow_install_hint(), mContext);
        saveOrRemove(Pref.LOG_LEVEl, config.getLogLevel(), mContext);
        saveOrRemove(Pref.NEW_USER_RECOMMEND, config.getNew_user_recommend(), mContext);
        saveOrRemove(Pref.SHOW_RECOMMEND, config.getShow_recommend(), mContext);
        saveOrRemove(Pref.SAVE_LAYOUT_COUNT, config.getSave_layout_count(), mContext);

        long time = System.currentTimeMillis();
        Pref.saveString(Pref.UPDATE_TIME, String.valueOf(time),
                mContext);
    }

    private static void saveOrRemove(String key, String value, Context context) {
        if (TextUtils.isEmpty(value)) {
            Pref.removeString(key, context);
        } else {
            Pref.saveString(key, value, context);
        }

    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getAppinfo_report_cycle() {
        return appinfo_report_cycle;
    }

    public void setAppinfo_report_cycle(String appinfo_report_cycle) {
        this.appinfo_report_cycle = appinfo_report_cycle;
    }

    public String getShow_other_app_switch() {
        return show_other_app_switch;
    }

    public void setShow_other_app_switch(String show_other_app_switch) {
        this.show_other_app_switch = show_other_app_switch;
    }

    public String getAd_cycle() {
        return ad_cycle;
    }

    public void setAd_cycle(String ad_cycle) {
        this.ad_cycle = ad_cycle;
    }

    public void setConfig_fetch_cycle(String config_fetch_cycle) {
        this.config_fetch_cycle = config_fetch_cycle;
    }

    public String getUninstall_app_list() {
        if (uninstall_app_list == null) {
            return null;
        } else {
            return new Gson().toJson(uninstall_app_list);
        }
    }

    public void setUninstall_app_list(List<String> uninstall_app_list) {
        this.uninstall_app_list = uninstall_app_list;
    }

    public String getLayout_code() {
        return layout_code;
    }

    public void setLayout_code(String layout_code) {
        this.layout_code = layout_code;
    }

    public String getShow_sign_in() {
        return show_sign_in;
    }

    public void setShow_sign_in(String show_sign_in) {
        this.show_sign_in = show_sign_in;
    }

    public String getQq_group() {
        return qq_group;
    }

    public void setQq_group(String qq_group) {
        this.qq_group = qq_group;
    }

    public String getHashTime() {
        return hashTime;
    }

    public void setHashTime(String hashTime) {
        this.hashTime = hashTime;
    }

    public void setShow_recommend(String show_recommend) {
        this.show_recommend = show_recommend;
    }
}


 