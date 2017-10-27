package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * Created by lmh on 2017/10/27.
 */

public class SoftwaresByTypeEntity implements Serializable {
    private String pageNo;
    private String pageSize;
    private String firstTypeCode;
    private String childTypeCode;
    private String deviceType;
    private String orderType;

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

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

    @Override
    public String toString() {
        return "SoftwaresByTypeEntity{" +
                "pageNo='" + pageNo + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", firstTypeCode='" + firstTypeCode + '\'' +
                ", childTypeCode='" + childTypeCode + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
