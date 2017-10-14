package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 应用类型
 */
public class SoftwareType implements Serializable {

    private String id;
    private String orderType;
    private String name;
    private String limit;
    private String firstTypeCodes;
    private String childTypeCodes;
    private String deviceTypes;
    private List<RankElements> rankElements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
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

    public List<RankElements> getRankElements() {
        return rankElements;
    }

    public void setRankElements(List<RankElements> rankElements) {
        this.rankElements = rankElements;
    }

    @Override
    public String toString() {
        return "SoftwareType{" +
                "id='" + id + '\'' +
                ", orderType='" + orderType + '\'' +
                ", name='" + name + '\'' +
                ", limit='" + limit + '\'' +
                ", firstTypeCodes='" + firstTypeCodes + '\'' +
                ", childTypeCodes='" + childTypeCodes + '\'' +
                ", deviceTypes='" + deviceTypes + '\'' +
                ", rankElements=" + rankElements +
                '}';
    }
}
