package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 软件排行列表解析类
 * Created by lmh on 2016/4/25.
 */
public class RankListInfo implements Serializable {

    private List<SoftwareType> ranks;

    public List<SoftwareType> getRanks() {
        return ranks;
    }

    public void setRanks(List<SoftwareType> ranks) {
        this.ranks = ranks;
    }

    @Override
    public String toString() {
        return "RankListInfo{" +
                "ranks=" + ranks +
                '}';
    }
}
