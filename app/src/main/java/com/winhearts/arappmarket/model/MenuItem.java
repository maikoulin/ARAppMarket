package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单栏子项
 */
public class MenuItem implements Serializable {

    //public final static String SELF_DEFINE = "";

    /**
     *
     */
    private static final long serialVersionUID = 5855936247816699150L;
    private int sort;
    private int homePage;
    private String name;
    private int hasChild;
    private String menuId;
    private String parentMenuId;


    /**
     * SELF_DEFINE - 自定义
     * TOPIC - 专题
     * SOFTWARE_TYPE - 软件分类
     */
    private String menuDataType;

    private SoftwareTypeInfo softwareTypeInfo;
    private Topic topicInfo;
    private List<Screen> screens;
    private List<MenuItem> child;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getHomePage() {
        return homePage;
    }

    public void setHomePage(int homePage) {
        this.homePage = homePage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    /**
     * @return SELF_DEFINE - 自定义
     * TOPIC - 专题
     * SOFTWARE_TYPE - 软件分类
     */
    public String getMenuDataType() {
        return menuDataType;
    }

    public void setMenuDataType(String menuDataType) {
        this.menuDataType = menuDataType;
    }


    public List<MenuItem> getChild() {
        return child;
    }

    public void setChild(ArrayList<MenuItem> child) {
        this.child = child;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void setScreens(ArrayList<Screen> screens) {
        this.screens = screens;
    }

    public SoftwareTypeInfo getSoftwareTypeInfo() {
        return softwareTypeInfo;
    }

    public void setSoftwareTypeInfo(SoftwareTypeInfo softwareTypeInfo) {
        this.softwareTypeInfo = softwareTypeInfo;
    }

    public Topic getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(Topic topicInfo) {
        this.topicInfo = topicInfo;
    }

    public void setChild(List<MenuItem> child) {
        this.child = child;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public void setScreens(List<Screen> screens) {
        this.screens = screens;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "sort=" + sort +
                ", homePage=" + homePage +
                ", name='" + name + '\'' +
                ", hasChild=" + hasChild +
                ", menuId='" + menuId + '\'' +
                ", parentMenuId='" + parentMenuId + '\'' +
                ", menuDataType='" + menuDataType + '\'' +
                ", softwareTypeInfo=" + (softwareTypeInfo != null ? softwareTypeInfo.toString() : "null") +
                ", topicInfo=" + topicInfo +
                ", screens=" + screens +
                ", child=" + child +
                '}';
    }
}
