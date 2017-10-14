package com.winhearts.arappmarket.model;

/**
 * vms版本检查返回信息类
 */
public class AppUpdate {
    private String versionName;//版本名
    private String versionCode;//版本号
    private String versionDesc;//版本内容描述
    private String updateType;//更新类型
    private String updateUrl;
    private String result;//是否要升级
    private String packageSize;//目标版本大小

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 版本内容描述
     *
     * @return
     */
    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }


    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    @Override
    public String toString() {
        return "AppUpdate [versionName=" + versionName + ", versionCode=" + versionCode + ", versionDesc=" + versionDesc + ", updateType=" + updateType + ", updateUrl=" + updateUrl + ", result=" + result + ", packageSize=" + packageSize + "]";
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }


}
