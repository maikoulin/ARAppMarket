package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 专题列表类型解析实体类
 * Created by lmh on 2016/4/25.
 */
public class TopicListInfo implements Serializable {
    private String orderType;
    private String type;
    private List<Topic> topics;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "TopicListInfo{" +
                "orderType='" + orderType + '\'' +
                ", type='" + type + '\'' +
                ", topics=" + topics +
                '}';
    }
}
