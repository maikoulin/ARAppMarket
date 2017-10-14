package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 安装信息
 * Created by lmh on 2015/7/24.
 */
public class InstallUrlEntity {

    public class Element {
        private String downloadUrl;
        private String packageName;

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "downloadUrl='" + downloadUrl + '\'' +
                    ", packageName='" + packageName + '\'' +
                    '}';
        }
    }

    private List<Element> softwares;

    public List<Element> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<Element> softwares) {
        this.softwares = softwares;
    }

    @Override
    public String toString() {
        return "InstallUrlEntity{" +
                "softwares=" + softwares +
                '}';
    }
}
