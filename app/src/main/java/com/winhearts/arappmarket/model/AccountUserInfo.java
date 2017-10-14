package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 帐号信息类
 */
public class AccountUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String wsId;// "网宿id",
    private String nickName;// "昵称",
    private String phoneNum;// "手机号",
    private String email;// "邮箱",
    private String regTime;// "注册时间",
    private String regIp;// "注册IP",
    private String lastLoginTime;// "最后登录时间",
    private String thirdPartyType;// "GZGD",
    private String thirdPartyId;// "第三方ID",
    private String regSource;// "注册来源"
    private String custId;//客户ID
    private String credits;//客户积分

    public String getWsId() {


        return wsId;
    }

    public void setWsId(String wsId) {
        this.wsId = wsId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phone) {
        this.phoneNum = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getThirdType() {
        return thirdPartyType;
    }

    public void setThirdType(String thirdType) {
        this.thirdPartyType = thirdType;
    }

    public String getThirdId() {
        return thirdPartyId;
    }

    public void setThirdId(String thirdId) {
        this.thirdPartyId = thirdId;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
    }

    public String getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(String thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "AccountUserInfo{" +
                "wsId='" + wsId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", email='" + email + '\'' +
                ", regTime='" + regTime + '\'' +
                ", regIp='" + regIp + '\'' +
                ", lastLoginTime='" + lastLoginTime + '\'' +
                ", thirdPartyType='" + thirdPartyType + '\'' +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                ", regSource='" + regSource + '\'' +
                ", custId='" + custId + '\'' +
                ", credits='" + credits + '\'' +
                '}';
    }
}
