package com.winhearts.arappmarket.model;

/**
 * 版本信息
 */
public class VersionNo {
    private String versionNo;
    private String terminalCode;

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    @Override
    public String toString() {
        return "VersionNo{" +
                "versionNo='" + versionNo + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                '}';
    }
}
