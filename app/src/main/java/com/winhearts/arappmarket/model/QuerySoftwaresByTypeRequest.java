package com.winhearts.arappmarket.model;

/**
 * 通过类型查询应用
 */
public class QuerySoftwaresByTypeRequest {

    private String firstTypeCode;
    private String childTypeCode;
    private String pageNo;
    private String pageSize;
    private String orderType;
    private String deviceType;

    public String getFirstTypeCode() {
        return firstTypeCode;
    }

    public void setFirstTypeCode(String firstType) {
        this.firstTypeCode = firstType;
    }

    public String getChildTypeCode() {
        return childTypeCode;
    }

    public void setChildTypeCode(String childType) {
        this.childTypeCode = childType;
    }

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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
