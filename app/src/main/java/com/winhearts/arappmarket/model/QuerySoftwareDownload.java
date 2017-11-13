package com.winhearts.arappmarket.model;

import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.utils.MacUtil;

/**
 * 信息
 */
public class QuerySoftwareDownload {
    private String packageName;
    private String mac;
    private String winId;

    public QuerySoftwareDownload() {
        mac = MacUtil.getMacAddress();
        winId = ConstInfo.accountWinId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getwinId() {
        return winId;
    }

    public void setwinId(String winId) {
        this.winId = winId;
    }

    @Override
    public String toString() {
        return "QuerySoftwareDownload{" +
                "packageName='" + packageName + '\'' +
                ", mac='" + mac + '\'' +
                ", winId='" + winId + '\'' +
                '}';
    }
}
