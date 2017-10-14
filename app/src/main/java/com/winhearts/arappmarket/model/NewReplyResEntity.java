package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 获取用户新消息返回的信息，新消息指跟用户相关的评论消息,底部有示例
 * Created by suxq on 2017/3/8.
 */

public class NewReplyResEntity implements Serializable {

    //这次调用接口时服务器的时间
    private long currentTime;
    //该用户相关评论
    private List<RelatedReply> relatedReply;

    /**
     * 跟用户相关的评论信息
     */
    public static class RelatedReply implements Serializable {

        //评论ID
        private long commentId;
        //评论回复列表
        private List<ReplyContent> replyList;

        public long getCommentId() {
            return commentId;
        }

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }

        public List<ReplyContent> getReplyList() {
            return replyList;
        }

        public void setReplyList(List<ReplyContent> replyList) {
            this.replyList = replyList;
        }

        @Override
        public String toString() {
            return "RelatedReply{" +
                    "commentId='" + commentId + '\'' +
                    ", replyList=" + replyList +
                    '}';
        }
    }

    /**
     * 每条评论的信息
     */
    public static class ReplyContent implements Serializable {
        //回复消息的数据库ID
        private long id;
        //回复对象
        private String toUser;
        //回复用户
        private String fromUser;
        //回复时间
        private long time;
        //回复内容
        private String content;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

        @Override
        public String toString() {
            return "ReplyContent{" +
                    "id=" + id +
                    ", toUser='" + toUser + '\'' +
                    ", fromUser='" + fromUser + '\'' +
                    ", time=" + time +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public List<RelatedReply> getRelatedReply() {
        return relatedReply;
    }

    public void setRelatedReply(List<RelatedReply> relatedReply) {
        this.relatedReply = relatedReply;
    }

    @Override
    public String toString() {
        return "NewReplyResEntity{" +
                "currentTime=" + currentTime +
                ", relatedReply=" + relatedReply +
                '}';
    }

    /*
    {
    "currentTime": 1489476733517,
    "relatedReply": [
        {
            "commentId": 504761,
            "replyList": [
                {
                    "id" : 500001,
                    "toUser": "20015487",
                    "fromUser": "4189718",
                    "time": 1489460495701,
                    "content": "哈哈，你们帅有用吗？哈哈，你们帅有用吗？哈哈，你们帅有用吗？哈哈，你们帅有用吗？哈哈，你们帅有用吗？哈哈，你们帅有用吗？哈哈，你们帅有用吗？"
                },
                {
                    "id" : 500002,
                    "toUser": "20015487",
                    "fromUser": "4189718",
                    "time": 1489460421808,
                    "content": "哈哈，你们帅有用吗？"
                },
                {
                    "id" : 500003,
                    "toUser": "20015487",
                    "fromUser": "4189718",
                    "time": 1489458773898,
                    "content": "是的"
                }
            ]
        }
    ]
    }
     */
}
