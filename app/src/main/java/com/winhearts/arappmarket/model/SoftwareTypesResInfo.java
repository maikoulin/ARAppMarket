package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 软件类型数据
 * Created by lmh on 2016/3/28.
 */
public class SoftwareTypesResInfo implements Serializable {

    private String orderType;
    private String defaultIndex;

    private ArrayList<SoftwareType> softwareTypes;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDefaultIndex() {
        return defaultIndex;
    }

    public void setDefaultIndex(String defaultIndex) {
        this.defaultIndex = defaultIndex;
    }

    public ArrayList<SoftwareType> getSoftwareTypes() {
        return softwareTypes;
    }

    public void setSoftwareTypes(ArrayList<SoftwareType> softwareTypes) {
        this.softwareTypes = softwareTypes;
    }

    @Override
    public String toString() {
        return "SoftwareTypesResInfo{" +
                "orderType='" + orderType + '\'' +
                ", defaultIndex='" + defaultIndex + '\'' +
                ", softwareTypes=" + softwareTypes +
                '}';
    }
}
