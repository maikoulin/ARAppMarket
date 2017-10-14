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
    private String wsId;
    private String loginToken;
    private String loginType;


    public String getWsId() {
        return wsId;
    }

    public void setWsId(String wsId) {
        this.wsId = wsId;
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
                "wsId='" + wsId + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
