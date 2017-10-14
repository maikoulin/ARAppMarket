package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 点击上报
 * Created by lmh on 2015/7/23.
 */
public class UploadClick implements Serializable, Cloneable {
    private String type;
    private String moduleId;
    private String click;
    private String time;
    private String modulePath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    @Override
    public String toString() {
        return "UploadClick{" +
                "type='" + type + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", click='" + click + '\'' +
                ", time='" + time + '\'' +
                ", modulePath='" + modulePath + '\'' +
                '}';
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
