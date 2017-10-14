package com.winhearts.arappmarket.model;

import java.util.ArrayList;

/**
 * 推荐应用接口请求参数
 */
public class ClientInfoForRecommendApp {
    public ClientInfoForRecommendApp(ArrayList<String> packageNames) {

        this.packageNames = packageNames;
    }

    public ArrayList<String> getPackageNames() {

        return packageNames;
    }

    public void setPackageNames(ArrayList<String> packageNames) {
        this.packageNames = packageNames;
    }

    private ArrayList<String> packageNames;
}

