package com.winhearts.arappmarket.model;

import java.util.ArrayList;

/**
 *
 */
public class PackageList {
    private String version;
    private ArrayList<String> onlinePackages;
    private ArrayList<String> offlinePackages;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getOnlinePackages() {
        return onlinePackages;
    }

    public void setOnlinePackages(ArrayList<String> onlinePackages) {
        this.onlinePackages = onlinePackages;
    }

    public ArrayList<String> getOfflinePackages() {
        return offlinePackages;
    }

    public void setOfflinePackages(ArrayList<String> offlinePackages) {
        this.offlinePackages = offlinePackages;
    }

    @Override
    public String toString() {
        return "PackageList{" +
                "version='" + version + '\'' +
                ", onlinePackages=" + onlinePackages +
                ", offlinePackages=" + offlinePackages +
                '}';
    }
}
