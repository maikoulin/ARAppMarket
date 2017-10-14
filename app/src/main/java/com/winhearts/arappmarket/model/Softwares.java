package com.winhearts.arappmarket.model;

import java.util.List;

/**
 * 应用集合
 */
public class Softwares {
    private int totalCount;
    private List<SoftwareInfo> softwares;
    private String autoUpgrade;

    public String getAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(String autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public List<SoftwareInfo> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<SoftwareInfo> softwares) {
        this.softwares = softwares;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Softwares{" +
                "totalCount=" + totalCount +
                ", softwares=" + softwares +
                ", autoUpgrade='" + autoUpgrade + '\'' +
                '}';
    }
}
