package com.winhearts.arappmarket.model;

import com.winhearts.arappmarket.utils.common.Utils;

import java.io.Serializable;
import java.util.Map;

/**
 * 订单变化返回信息，用于支付模块
 * Created by zhuangwy on 2016/6/15.
 */
public class OrderChangeResEntity implements Serializable {

    private String url;
    private String payReqType;
    private String payDescription;
    private Map<String, String> showInfo;
    private long orderTime;
    private String orderCode;
    private String channelTypeName;


    public String getChannelTypeName() {
        return channelTypeName;
    }

    public void setChannelTypeName(String channelTypeName) {
        this.channelTypeName = channelTypeName;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayReqType() {
        return payReqType;
    }

    public void setPayReqType(String payReqType) {
        this.payReqType = payReqType;
    }

    public String getPayDescription() {
        return payDescription;
    }

    public void setPayDescription(String payDescription) {
        this.payDescription = payDescription;
    }

    public Map<String, String> getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(Map<String, String> showInfo) {
        this.showInfo = showInfo;
    }

    @Override
    public String toString() {
        return "OrderChangeResEntity{" +
                "url='" + url + '\'' +
                ", payReqType='" + payReqType + '\'' +
                ", payDescription='" + payDescription + '\'' +
                ", showInfo=" + Utils.mapToString(showInfo) +
                ", orderTime='" + orderTime + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", channelTypeName='" + channelTypeName + '\'' +
                '}';
    }
}

