package com.winhearts.arappmarket.event;

/**
 * rxjava消息传递，用于应用安装
 * Created by lmh on 2016/4/1.
 */
public class InstallEvent {

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public InstallEvent(String packageName) {
        this.packageName = packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "InstallEvent{" +
                "packageName='" + packageName + '\'' +
                '}';
    }
}
