package com.winhearts.arappmarket.download.manage;

import com.winhearts.arappmarket.model.StateInfo;

/**
 * Description:
 * Created by lmh on 2016/3/30.
 */
public class ManagerDownloadEvent {


    public static final int  TYPE_DOWNLOAD = 1;
    public static final int TYPE_INSTALL = 2;
    /**
     * 废弃状态
     */
    @Deprecated
    public static final int TYPE_ADD = 3;
    public static final int TYPE_REMOVE = 4;

    private StateInfo stateInfo;
    private String actString;
    private String packageName;

    /**
     * 1,下载广播 DisplayConfig.ACTION_RESULT_MESSAGE
     * 2,安装广播 PackageUtils.INSTALL
     * 3,安装广播 android.intent.action.PACKAGE_ADDED
     */
    public int type;

    public ManagerDownloadEvent(int type, StateInfo stateInfo, String packageName, String actString) {
        this.stateInfo = stateInfo;
        this.actString = actString;
        this.type = type;
        this.packageName = packageName;
    }

    public StateInfo getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(StateInfo stateInfo) {
        this.stateInfo = stateInfo;
    }

    public String getActString() {
        return actString;
    }

    public void setActString(String actString) {
        this.actString = actString;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ManagerDownloadEvent{" +
                "stateInfo=" + stateInfo +
                ", actString='" + actString + '\'' +
                ", packageName='" + packageName + '\'' +
                ", type=" + type +
                '}';
    }
}
