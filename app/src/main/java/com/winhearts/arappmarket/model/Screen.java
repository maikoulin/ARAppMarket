package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 主页界面
 */
public class Screen implements Serializable, Comparable<Screen> {

    /**
     *
     */
    private static final long serialVersionUID = 2244272568953580106L;

    private int sort;

    private int row;

    private int col;
    private String screenId;
    private ArrayList<Element> elementResInfoList;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public ArrayList<Element> getElementResInfoList() {
        return elementResInfoList;
    }

    public void setElementResInfoList(ArrayList<Element> elementResInfoList) {
        this.elementResInfoList = elementResInfoList;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public int compareTo(Screen another) {

        if (this.getSort() > another.getSort()) {
            return 1;
        } else if (this.getSort() < another.getSort()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Screen{" +
                "sort=" + sort +
                ", row=" + row +
                ", col=" + col +
                ", screenId='" + screenId + '\'' +
                ", elementResInfoList=" + elementResInfoList +
                '}';
    }
}
