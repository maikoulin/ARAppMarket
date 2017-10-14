package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 应用信息
 */
public class SoftwareInfo implements Serializable {

    private static final long serialVersionUID = 5139228235774189748L;
    private String packageName;
    private String name;
    private String download;
    private String developor;
    private String language;
    private String firstTypeName;
    private String childTypeName;
    private String versionCode;
    private String versionName;
    private String size;
    private String minFireware;
    private String maxFireware;
    private String description;
    private String updateDescription;
    private String updateTime;
    private String icon;
    private String previews;
    private String star;
    private String childTypeCode;
    private String firstTypeCode;
    private String deviceType;
    private String deviceTypeName;
    private boolean isWhite;

    private boolean isInstalled;
    private int mode;
    private String needUpdate;
    private DownRecordInfo downRecordInfo;
    private String tag;

    private long installTime;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDevelopor() {
        return developor;
    }

    public void setDevelopor(String developor) {
        this.developor = developor;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMinFireware() {
        return minFireware;
    }

    public void setMinFireware(String minFireware) {
        this.minFireware = minFireware;
    }

    public String getMaxFireware() {
        return maxFireware;
    }

    public void setMaxFireware(String maxFireware) {
        this.maxFireware = maxFireware;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPreviews() {
        return previews;
    }

    public void setPreviews(String previews) {
        this.previews = previews;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getChildTypeCode() {
        return childTypeCode;
    }

    public void setChildTypeCode(String childTypeCode) {
        this.childTypeCode = childTypeCode;
    }

    public String getFirstTypeCode() {
        return firstTypeCode;
    }

    public void setFirstTypeCode(String firstTypeCode) {
        this.firstTypeCode = firstTypeCode;
    }

    public String getFirstTypeName() {
        return firstTypeName;
    }

    public void setFirstTypeName(String firstTypeName) {
        this.firstTypeName = firstTypeName;
    }

    public String getChildTypeName() {
        return childTypeName;
    }

    public void setChildTypeName(String childTypeName) {
        this.childTypeName = childTypeName;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(String needUpdate) {
        this.needUpdate = needUpdate;
    }

    public DownRecordInfo getDownRecordInfo() {
        return downRecordInfo;
    }

    public void setDownRecordInfo(DownRecordInfo downRecordInfo) {
        this.downRecordInfo = downRecordInfo;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setIsWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setIsInstalled(boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

    @Override
    public String toString() {
        return "SoftwareInfo{" +
                "packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", download='" + download + '\'' +
                ", developor='" + developor + '\'' +
                ", language='" + language + '\'' +
                ", firstTypeName='" + firstTypeName + '\'' +
                ", childTypeName='" + childTypeName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", size='" + size + '\'' +
                ", minFireware='" + minFireware + '\'' +
                ", maxFireware='" + maxFireware + '\'' +
                ", description='" + description + '\'' +
                ", updateDescription='" + updateDescription + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", icon='" + icon + '\'' +
                ", previews='" + previews + '\'' +
                ", star='" + star + '\'' +
                ", childTypeCode='" + childTypeCode + '\'' +
                ", firstTypeCode='" + firstTypeCode + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceTypeName='" + deviceTypeName + '\'' +
                ", isWhite=" + isWhite +
                ", isInstalled=" + isInstalled +
                ", mode=" + mode +
                ", needUpdate='" + needUpdate + '\'' +
                ", downRecordInfo=" + downRecordInfo +
                ", tag='" + tag + '\'' +
                ", installTime=" + installTime +
                '}';
    }
}
