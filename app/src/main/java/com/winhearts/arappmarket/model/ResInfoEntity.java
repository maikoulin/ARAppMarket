package com.winhearts.arappmarket.model;

import java.util.List;
import java.util.Map;

/**
 * Description:资原应用信息
 * Created by lmh on 2016/3/29.
 */
public class ResInfoEntity {

    /**
     * type : ACTION
     * cmp :
     * action :
     * categorys :
     * data : type为DATA时
     * params : {"name":"value"}
     * packageName : com.ott.ams
     * name : test app
     * icon : http://ssss.png
     */

    private List<AppsBean> apps;

    public List<AppsBean> getApps() {
        return apps;
    }

    public void setApps(List<AppsBean> apps) {
        this.apps = apps;
    }

    public static class AppsBean {
        private String type;
        private String cmp;
        private String action;
        private String categorys;
        private String data;

        //本地插入字段与json无关   已安装1 未安装0。 用来排序
        private Integer installType;

        /**
         * name : value
         */
        private Map<String, String> params;
        private String packageName;
        private String name;
        private String icon;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCmp() {
            return cmp;
        }

        public void setCmp(String cmp) {
            this.cmp = cmp;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getCategorys() {
            return categorys;
        }

        public void setCategorys(String categorys) {
            this.categorys = categorys;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Integer getInstallType() {
            return installType;
        }

        public void setInstallType(int installType) {
            this.installType = installType;
        }

        @Override
        public String toString() {
            return "AppsBean{" +
                    "type='" + type + '\'' +
                    ", cmp='" + cmp + '\'' +
                    ", action='" + action + '\'' +
                    ", categorys='" + categorys + '\'' +
                    ", data='" + data + '\'' +
                    ", installType=" + installType +
                    ", params=" + params +
                    ", packageName='" + packageName + '\'' +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResInfoEntity{" +
                "apps=" + apps +
                '}';
    }
}
