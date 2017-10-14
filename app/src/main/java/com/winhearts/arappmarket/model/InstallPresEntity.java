package com.winhearts.arappmarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 预安装应用
 * Created by lmh on 2015/7/23.
 */
public class InstallPresEntity implements Serializable {

    private String version;
    private List<Element> pres;

    public List<Element> getPres() {
        return pres;
    }

    public void setPres(List<Element> pres) {
        this.pres = pres;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "InstallPresEntity{" +
                "pres=" + pres +
                ", version='" + version + '\'' +
                '}';
    }

    public class Element {
        String packageName;
        String preType;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPreType() {
            return preType;
        }

        public void setPreType(String preType) {
            this.preType = preType;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "packageName='" + packageName + '\'' +
                    ", preType='" + preType + '\'' +
                    '}';
        }
    }


}
