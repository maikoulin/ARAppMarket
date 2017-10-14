package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 下载记录信息
 */
public class DownRecordInfo implements Serializable {
    private String appName;
    private String downloadUrl;
    private String iconUrl;
    private String fileName;
    private String packageName = "";
    private int downlength;
    private int fileSize;
    private int state;
    private String appVersion;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getDownlength() {
        return downlength;
    }

    public void setDownlength(int downlength) {
        this.downlength = downlength;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "DownRecordInfo{" +
                "appName='" + appName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", downlength=" + downlength +
                ", fileSize=" + fileSize +
                ", state=" + state +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
