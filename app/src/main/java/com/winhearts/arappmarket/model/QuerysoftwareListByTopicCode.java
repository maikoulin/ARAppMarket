package com.winhearts.arappmarket.model;

/**
 * 查询软件，通过专辑code
 */
public class QuerysoftwareListByTopicCode {
    private String pageNo;
    private String pageSize;
    private String code;

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
