package com.winhearts.arappmarket.event;

/**
 * rxjava消息传递，用于我的应用中，应用打开通知
 * Created by lmh on 2016/8/31.
 */
public class OpenAPkEvent {

    private String packageName;

    public OpenAPkEvent(String packageName) {
        this.packageName = packageName;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
