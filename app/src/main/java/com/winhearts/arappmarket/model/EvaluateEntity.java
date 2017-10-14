package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 评论实体类
 * Created by lmh on 2015/8/9.
 */
public class EvaluateEntity implements Serializable {


    private List<EvaluateContent> comments;
    private String totalCount;

    public class EvaluateContent implements Serializable {
        private String versionName;
        private String star;
        private String content;
        private String nickName;
        private String time;

        private String commentId;
        private String replyCount;

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getStar() {
            return star;
        }

        public void setStar(String star) {
            this.star = star;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(String replyCount) {
            this.replyCount = replyCount;
        }

        @Override
        public String toString() {
            return "EvaluateContent{" +
                    "versionName='" + versionName + '\'' +
                    ", star='" + star + '\'' +
                    ", content='" + content + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", time='" + time + '\'' +
                    ", commentId='" + commentId + '\'' +
                    ", replyCount='" + replyCount + '\'' +
                    '}';
        }
    }

    public List<EvaluateContent> getComments() {
        return comments;
    }

    public void setComments(List<EvaluateContent> comments) {
        this.comments = comments;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "EvaluateEntity{" +
                "comments=" + comments +
                ", totalCount='" + totalCount + '\'' +
                '}';
    }
}
