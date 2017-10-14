package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 软件类型数据
 * Created by lmh on 2016/3/28.
 */
public class SoftwareResInfo implements Serializable {

    private String specialRecom;
    private String loop;
    private List<SoftwareInfo> softwareInfos;

    public String getSpecialRecom() {
        return specialRecom;
    }

    public void setSpecialRecom(String specialRecom) {
        this.specialRecom = specialRecom;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public List<SoftwareInfo> getSoftwareInfos() {
        return softwareInfos;
    }

    public void setSoftwareInfos(List<SoftwareInfo> softwareInfos) {
        this.softwareInfos = softwareInfos;
    }

    @Override
    public String toString() {
        return "SoftwareResInfo{" +
                "specialRecom='" + specialRecom + '\'' +
                ", loop='" + loop + '\'' +
                ", softwareInfos=" + softwareInfos +
                '}';
    }
}
