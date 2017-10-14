package com.winhearts.arappmarket.model;


/**
 * 应用详细类
 */
public class AppDetail {
    private String size;
    private String updateTime;
    private String score;
    private String downloadCounts;
    private String company;
    private String detailName;
    private String desc;
    private SlideImages slideImages;
    private String iconUrl;
    private String downloadUrl;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDownloadCounts() {
        return downloadCounts;
    }

    public void setDownloadCounts(String downloadCounts) {
        this.downloadCounts = downloadCounts;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public SlideImages getSlideImages() {
        return slideImages;
    }

    public void setSlideImages(SlideImages slideImages) {
        this.slideImages = slideImages;
    }
}
