package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 广告信息类
 */
public class AdvertisementEntity implements Serializable {

    private List<Content> list;

    public List<Content> getList() {
        return list;
    }

    public void setList(List<Content> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "AdvertisementEntity{" +
                "list=" + list +
                '}';
    }

    public class Content implements Serializable {

        private String type;
        private String imageUrl;
        private String id;
        private String description;
        private SoftWare software;
        private Topic topic;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public SoftWare getSoftware() {
            return software;
        }

        public void setSoftware(SoftWare software) {
            this.software = software;
        }

        public Topic getTopic() {
            return topic;
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }

        @Override
        public String toString() {
            return "Content{" +
                    "type='" + type + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", id='" + id + '\'' +
                    ", description='" + description + '\'' +
                    ", software=" + software +
                    ", topic=" + topic +
                    '}';
        }
    }

    public class SoftWare implements Serializable {
        private String name;
        private String packageName;
        private String size;
        private String download;
        private String versionName;
        private String star;
        private String updateTime;
        private String icon;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "SoftWare{" +
                    "name='" + name + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", size='" + size + '\'' +
                    ", download='" + download + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", star='" + star + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

//    public class Topic implements Serializable {
//        private String name;
//        private String code;
//        private String description;
//        private String imageUrl;
//        private String createTime;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getCode() {
//            return code;
//        }
//
//        public void setCode(String code) {
//            this.code = code;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public void setDescription(String description) {
//            this.description = description;
//        }
//
//        public String getImageUrl() {
//            return imageUrl;
//        }
//
//        public void setImageUrl(String imageUrl) {
//            this.imageUrl = imageUrl;
//        }
//
//        public String getCreateTime() {
//            return createTime;
//        }
//
//        public void setCreateTime(String createTime) {
//            this.createTime = createTime;
//        }
//
//        @Override
//        public String toString() {
//            return "Topic{" +
//                    "name='" + name + '\'' +
//                    ", code='" + code + '\'' +
//                    ", description='" + description + '\'' +
//                    ", imageUrl='" + imageUrl + '\'' +
//                    ", createTime='" + createTime + '\'' +
//                    '}';
//        }
//    }

}
