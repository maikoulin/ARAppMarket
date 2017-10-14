package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 促成下载记录路径
 * Created by lmh on 2016/5/11.
 */
public class DownloadPath implements Serializable {

    private String layoutId;
    private String menuId;
    private String subMenuId;
    private String moduleId;
    private String modulePath;

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getSubMenuId() {
        return subMenuId;
    }

    public void setSubMenuId(String subMenuId) {
        this.subMenuId = subMenuId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    @Override
    public String toString() {
        return "DownloadPath{" +
                "layoutId='" + layoutId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", subMenuId='" + subMenuId + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", modulePath='" + modulePath + '\'' +
                '}';
    }
}
