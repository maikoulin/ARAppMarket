package com.winhearts.arappmarket.model;

import java.util.ArrayList;

/**
 * 查询软件推荐
 */
public class QuerySoftwaresRecomment {
    private int totalCount;
    private ArrayList<SoftwareInfo> softwares;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "QuerySoftwaresRecomment{" +
                "totalCount=" + totalCount +
                ", softwares=" + softwares +
                '}';
    }

    public ArrayList<SoftwareInfo> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(ArrayList<SoftwareInfo> softwares) {
        this.softwares = softwares;
    }
}
