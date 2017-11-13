package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 账号系统
 *
 * @author liw
 */
public class AccountIDToken implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String winId;
    private String loginToken;
    private String loginType;


    public String getWinId() {
        return winId;
    }

    public void setWinId(String winId) {
        this.winId = winId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "AccountIDToken{" +
                "winId='" + winId + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
