package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 应用安装，用于批量获取
 * Created by lmh on 2015/7/28.
 */
public class InstallBatsEntity {
    private String mac;
    private String winId;
    private List<String> packageNames;

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

    public List<String> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    public String toString() {
        return "InstallBatsEntity{" +
                "mac='" + mac + '\'' +
                ", winId='" + winId + '\'' +
                ", packageNames=" + packageNames +
                '}';
    }
}
