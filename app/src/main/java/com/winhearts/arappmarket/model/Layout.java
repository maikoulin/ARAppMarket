package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 布局信息，用于主界面
 */
public class Layout implements Serializable {

    private static final long serialVersionUID = -8128599901803720007L;
    private String layoutId;
    private String currentVerNo;
    private String logo;
    private String startBkgImg;
    private String bg; //应用背景图片地址
    private String displayTime; //3.8 启动页展示时间，可能为空
    private String associate; //3.8 关联的包名，可能为空
    private List<MenuItem> menuInfos;


    public List<MenuItem> getMenuInfos() {
        return menuInfos;
    }

    public void setMenuInfos(List<MenuItem> menuInfos) {
        this.menuInfos = menuInfos;
    }

    public String getStartBkgImg() {
        return startBkgImg;
    }

    public void setStartBkgImg(String startBkgImg) {
        this.startBkgImg = startBkgImg;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCurrentVerNo() {
        return currentVerNo;
    }

    public void setCurrentVerNo(String currentVerNo) {
        this.currentVerNo = currentVerNo;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getAssociate() {
        return associate;
    }

    public void setAssociate(String associate) {
        this.associate = associate;
    }

    @Override
    public String toString() {
        return "Layout{" +
                "layoutId='" + layoutId + '\'' +
                ", currentVerNo='" + currentVerNo + '\'' +
                ", logo='" + logo + '\'' +
                ", startBkgImg='" + startBkgImg + '\'' +
                ", bg='" + bg + '\'' +
                ", displayTime='" + displayTime + '\'' +
                ", associate='" + associate + '\'' +
                ", menuInfos=" + menuInfos +
                '}';
    }
}
