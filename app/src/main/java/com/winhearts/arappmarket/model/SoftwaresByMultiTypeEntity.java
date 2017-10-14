package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 2.49	通过多个软件类型获取软件列表
 * Created by lmh on 2016/3/28.
 */
public class SoftwaresByMultiTypeEntity implements Serializable {

    private String start;
    private String end;
    private String firstTypeCodes;
    private String childTypeCodes;
    private String deviceTypes;
    private String orderType;
    private List<String> excludePackages;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getFirstTypeCodes() {
        return firstTypeCodes;
    }

    public void setFirstTypeCodes(String firstTypeCodes) {
        this.firstTypeCodes = firstTypeCodes;
    }

    public String getChildTypeCodes() {
        return childTypeCodes;
    }

    public void setChildTypeCodes(String childTypeCodes) {
        this.childTypeCodes = childTypeCodes;
    }

    public String getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(String deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    @Override
    public String toString() {
        return "SoftwaresByMultiTypeEntity{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", firstTypeCodes='" + firstTypeCodes + '\'' +
                ", childTypeCodes='" + childTypeCodes + '\'' +
                ", deviceTypes='" + deviceTypes + '\'' +
                ", orderType='" + orderType + '\'' +
                ", excludePackages=" + excludePackages +
                '}';
    }
}
