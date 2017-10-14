package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 日志上报信息类
 */
public class LogInfos {
    private String prodCode; //产品标识：必选",
    private String appVer; //终端版本号：必选",
    private String clientCode;//终端唯一标识：必选",
    private String clientOsVer;//操作系统：可选",
    private String clientType;// 型号：可选",
    private List<LogInfo> logList;// 日志信息列表

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getClientOsVer() {
        return clientOsVer;
    }

    public void setClientOsVer(String clientOsVer) {
        this.clientOsVer = clientOsVer;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public List<LogInfo> getLogList() {
        return logList;
    }

    public void setLogList(List<LogInfo> logList) {
        this.logList = logList;
    }

    @Override
    public String toString() {
        return "LogInfos{" +
                "prodCode='" + prodCode + '\'' +
                ", appVer='" + appVer + '\'' +
                ", clientCode='" + clientCode + '\'' +
                ", clientOsVer='" + clientOsVer + '\'' +
                ", clientType='" + clientType + '\'' +
                ", logList=" + logList +
                '}';
    }
}
