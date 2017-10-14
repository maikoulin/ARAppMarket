package com.winhearts.arappmarket.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 与我相关的评论消息实体类, 用于缓存数据到本地
 * Created by suxq on 2017/3/15.
 */

public class RelateReplyEntity implements Serializable, Comparable<RelateReplyEntity> {

    private long commentId;
    private long id;
    //回复对象
    private String toUser;
    //回复用户
    private String fromUser;
    //回复时间
    private long time;
    //回复内容
    private String content;
    //消息显示格式
    private String format;

    public RelateReplyEntity() {
    }

    public RelateReplyEntity(NewReplyResEntity.ReplyContent reply) {
        this.toUser = reply.getToUser();
        this.fromUser = reply.getFromUser();
        this.time = reply.getTime();
        this.content = reply.getContent();
        this.id = reply.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormat() {
        String str = (fromUser.substring(0, 1) + "***" + fromUser.substring(fromUser.length() - 1)) + "回复";
        if (TextUtils.isEmpty(getToUser())) {
            //一级回复消息
            str += "我的评论";
        } else {
            //二级回复消息
            str += toUser.equals("我") ? toUser : (toUser.substring(0, 1) + "***" + toUser.substring(toUser.length() - 1));
        }
        format = str;
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "RelateReplyEntity{" +
                "commentId=" + commentId +
                ", id=" + id +
                ", toUser='" + toUser + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                ", format='" + format + '\'' +
                '}';
    }

    /**
     * 按评论时间最近的开始排序
     */
    @Override
    public int compareTo(@NonNull RelateReplyEntity o) {
        return (this.time < o.time) ? 1 : (this.time == o.time) ? 0 : -1;
    }
}
