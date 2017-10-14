package com.winhearts.arappmarket.model;

/**
 * 单个日志信息
 */
public class LogInfo {
    private int id;
    private String title;// "日志标题：必选",
    private String level;//"日志级别：可选",
    private String tagCode;// "标签：可选",
    private String content;// "内容:可选"
    private String others;//"附加信息：可选"
    private String createTime;//创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", tagCode='" + tagCode + '\'' +
                ", content='" + content + '\'' +
                ", others='" + others + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
