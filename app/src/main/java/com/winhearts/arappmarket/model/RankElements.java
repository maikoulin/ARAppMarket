package com.winhearts.arappmarket.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 软件信息，用于软件排行列表
 * Created by lmh on 2016/1/19.
 */
public class RankElements implements Comparable<RankElements>, Serializable {
    private String index;
    private SoftwareInfo softwareInfo;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public SoftwareInfo getSoftwareInfo() {
        return softwareInfo;
    }

    public void setSoftwareInfo(SoftwareInfo softwareInfo) {
        this.softwareInfo = softwareInfo;
    }

    @Override
    public String toString() {
        return "RankElements{" +
                "index='" + index + '\'' +
                ", softwareInfo=" + softwareInfo +
                '}';
    }

    @Override
    public int compareTo(@NonNull RankElements another) {
        int lhsIndex = Integer.valueOf(this.getIndex());
        int rhsIndex = Integer.valueOf(another.getIndex());
        if (lhsIndex > rhsIndex) {
            return 1;
        }
        if (lhsIndex < rhsIndex) {
            return -1;
        }
        return 0;
    }
}
