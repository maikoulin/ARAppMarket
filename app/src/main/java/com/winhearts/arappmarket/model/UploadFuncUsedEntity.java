package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 功能上报
 * Created by lmh on 2016/3/23.
 */
public class UploadFuncUsedEntity implements Serializable {

    private String functionCode;
    private String clickTime;

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getClickTime() {
        return clickTime;
    }

    public void setClickTime(String clickTime) {
        this.clickTime = clickTime;
    }

    @Override
    public String toString() {
        return "UploadFuncUsedEntity{" +
                "functionCode='" + functionCode + '\'' +
                ", clickTime='" + clickTime + '\'' +
                '}';
    }
}
