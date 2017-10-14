package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 订单查询
 */
public class OrderQueryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String appKey;
    private String uid;
    private String orderCode;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Override
    public String toString() {
        return "OrderQueryEntity [appKey=" + appKey + ", uid=" + uid + ", orderCode=" + orderCode + "]";
    }

}
