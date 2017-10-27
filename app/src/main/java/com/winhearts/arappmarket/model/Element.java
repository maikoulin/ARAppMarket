package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 *  布局
 */
public class Element implements Serializable {

    private static final long serialVersionUID = -7167327447978751223L;
    // 元素Id
    private int id;
    // 起点坐标
    private int pLeft;
    // 起点坐标
    private int pTop;
    // 占用几行
    private int rowSpan;
    // 占用几列
    private int colSpan;
    // 资源图标
    private String resIcon;
    // 资源名称
    private String resName;

    /**
     * 资源类型
     * SOFTWARE - 软件
     * TOPIC -  专题
     * SOFTWARE_TYPE - 软件分类
     * RANK_TYPE -排行榜
     * ACTIVITY_TYPE - 活动
     * TOPIC_LIST 专题列表
     * RANK_LIST 排行榜列表
     */
    private String resType;

    //分类内容
    private String resInfo;
    private SoftwareInfo softwareInfo;

    private String elementId;
    private String menuId;
    private String parentMenuId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getpLeft() {
        return pLeft;
    }

    public void setpLeft(int pLeft) {
        this.pLeft = pLeft;
    }

    public int getpTop() {
        return pTop;
    }

    public void setpTop(int pTop) {
        this.pTop = pTop;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResIcon() {
        return resIcon;
    }

    public void setResIcon(String resIcon) {
        this.resIcon = resIcon;
    }

    /**
     * @return 资源类型
     * SOFTWARE - 软件
     * TOPIC -  专题
     * SOFTWARE_TYPE - 软件分类
     * RANK_TYPE -排行榜
     * TOPIC_LIST 专题列表
     * ACTIVITY_TYPE - 活动
     * RANK_LIST 排行榜列表
     */
    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
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

    public String getResInfo() {
        return resInfo;
    }

    public void setResInfo(String resInfo) {
        this.resInfo = resInfo;
    }

    public SoftwareInfo getSoftwareInfo() {
        return softwareInfo;
    }

    public void setSoftwareInfo(SoftwareInfo softwareInfo) {
        this.softwareInfo = softwareInfo;
    }

    @Override
    public String toString() {
        return "Element{" +
                "id=" + id +
                ", pLeft=" + pLeft +
                ", pTop=" + pTop +
                ", rowSpan=" + rowSpan +
                ", colSpan=" + colSpan +
                ", resIcon='" + resIcon + '\'' +
                ", resName='" + resName + '\'' +
                ", resType='" + resType + '\'' +
                ", resInfo='" + resInfo + '\'' +
                ", softwareInfo=" + softwareInfo +
                ", elementId='" + elementId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", parentMenuId='" + parentMenuId + '\'' +
                '}';
    }
}
