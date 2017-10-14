package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 专题列表集合
 */
public class TopicList {
    private List<Topic> list;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Topic> getTopicList() {
        return list;
    }

    public void setTopicList(List<Topic> topicList) {
        this.list = topicList;
    }

    @Override
    public String toString() {
        return "TopicList{" +
                "list=" + list +
                ", totalCount=" + totalCount +
                '}';
    }
}
