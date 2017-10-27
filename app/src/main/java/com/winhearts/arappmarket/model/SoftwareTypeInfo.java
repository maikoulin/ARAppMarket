package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 应用类型信息
 */
public class SoftwareTypeInfo implements Serializable {


    private static final long serialVersionUID = 3061560909303466526L;
    private String rootTypeCode;
    private String subTypeCode;
    private String handlerType;
    private String orderType;
    private String resName;
    private String rootTypeName;
    private String subTypeName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRootTypeCode() {
        return rootTypeCode;
    }

    public void setRootTypeCode(String rootTypeCode) {
        this.rootTypeCode = rootTypeCode;
    }

    public String getSubTypeCode() {
        return subTypeCode;
    }

    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
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

    public String getRootTypeName() {
        return rootTypeName;
    }

    public void setRootTypeName(String rootTypeName) {
        this.rootTypeName = rootTypeName;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }

    @Override
    public String toString() {
        return "SoftwareTypeInfo{" +
                "rootTypeCode='" + rootTypeCode + '\'' +
                ", subTypeCode='" + subTypeCode + '\'' +
                ", handlerType='" + handlerType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", resName='" + resName + '\'' +
                ", rootTypeName='" + rootTypeName + '\'' +
                ", subTypeName='" + subTypeName + '\'' +
                '}';
    }
}
