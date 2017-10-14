package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 创建订单请求返回信息
 */
public class OrderCreateRevalEntity {
    private String orderCode;// "201505121aptvxxxx",
    private String url;//
    private String isTest;

    private String defaultChannelType;

    private List<Channel> channels;

    public class Channel {
        private String channelTypeName;// "支付宝",
        private String channelType;// "ALIPAID"
        private String channelTypeShowName;//"贵州广电",
        private String payReqType;


        public String getChannelTypeShowName() {
            return channelTypeShowName;
        }

        public void setChannelTypeShowName(String channelTypeShowName) {
            this.channelTypeShowName = channelTypeShowName;
        }

        public String getPayReqType() {
            return payReqType;
        }

        public void setPayReqType(String payReqType) {
            this.payReqType = payReqType;
        }

        public String getChannelTypeName() {
            return channelTypeName;
        }

        public void setChannelTypeName(String channelTypeName) {
            this.channelTypeName = channelTypeName;
        }

        public String getChannelType() {
            return channelType;
        }

        public void setChannelType(String channelType) {
            this.channelType = channelType;
        }

        @Override
        public String toString() {
            return "Channel{" +
                    "channelTypeName='" + channelTypeName + '\'' +
                    ", channelType='" + channelType + '\'' +
                    ", channelTypeShowName='" + channelTypeShowName + '\'' +
                    ", payReqType='" + payReqType + '\'' +
                    '}';
        }
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

    @Override
    public String toString() {
        return "OrderCreateRevalEntity [orderCode=" + orderCode + ", url=" + url + ", isTest=" + isTest
                + ", defaultChannelType=" + defaultChannelType + ", channels=" + channels + "]";
    }

    public String getDefaultChannelType() {
        return defaultChannelType;
    }

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }

    public void setDefaultChannelType(String defaultChannelType) {
        this.defaultChannelType = defaultChannelType;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

}
