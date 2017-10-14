package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lmh on 2017/3/14.
 */

public class ReplyResEntity implements Serializable {
    private String nickName;
    private String content;
    private String time;
    private String appName;
    private String totalCount;
    private List<ReplyList> replyList;


    public class ReplyList implements Serializable {

        private String id;
        private String parentId;
        private String fromUser;
        private String toUser;
        private String time;
        private String content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getFromUser() {
            return fromUser;
        }

        public void setFromUser(String fromUser) {
            this.fromUser = fromUser;
        }

        public String getToUser() {
            return toUser;
        }

        public void setToUser(String toUser) {
            this.toUser = toUser;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ReplyList{" +
                    "id='" + id + '\'' +
                    ", parentId='" + parentId + '\'' +
                    ", fromUser='" + fromUser + '\'' +
                    ", toUser='" + toUser + '\'' +
                    ", time='" + time + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public List<ReplyList> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ReplyList> replyList) {
        this.replyList = replyList;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "ReplyResEntity{" +
                "nickName='" + nickName + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", appName='" + appName + '\'' +
                ", totalCount='" + totalCount + '\'' +
                ", replyList=" + replyList +
                '}';
    }
}
