package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 活动信息
 * Created by lmh on 2016/1/27.
 */
public class ActivityInfo implements Serializable {

    private String name;
    private String code;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ActivityInfo{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
