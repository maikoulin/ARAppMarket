package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 应用类型信息
 */
public class SoftwareTypeInfo implements Serializable {


    private static final long serialVersionUID = 3061560909303466526L;
    private String firstTypeCode;
    private String childTypeCode;
    private String deviceType;
    private String orderType;
    private String resName;
    private String firstTypeName;
    private String childTypeName;

    public String getFirstTypeCode() {
        return firstTypeCode;
    }

    public void setFirstTypeCode(String firstTypeCode) {
        this.firstTypeCode = firstTypeCode;
    }

    public String getChildTypeCode() {
        return childTypeCode;
    }

    public void setChildTypeCode(String childTypeCode) {
        this.childTypeCode = childTypeCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
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

    @Override
    public String toString() {
        return "SoftwareTypeInfo{" +
                "firstTypeCode='" + firstTypeCode + '\'' +
                ", childTypeCode='" + childTypeCode + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", resName='" + resName + '\'' +
                ", firstTypeName='" + firstTypeName + '\'' +
                ", childTypeName='" + childTypeName + '\'' +
                '}';
    }
}
