package com.winhearts.arappmarket.model;

/**
 * 第三方注册信息
 * Created by lmh on 2016/4/22.
 */
public class RegisterThirdEntity {
    private String thirdPartyType;
    private String thirdPartyId;
//    private String thirdInfo;


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

//    public String getThirdInfo() {
//        return thirdInfo;
//    }
//
//    public void setThirdInfo(String thirdInfo) {
//        this.thirdInfo = thirdInfo;
//    }

    @Override
    public String toString() {
        return "RegisterThirdEntity{" +
                "thirdPartyType='" + thirdPartyType + '\'' +
                ", thirdPartyId='" + thirdPartyId + '\'' +
//                ", thirdInfo='" + thirdInfo + '\'' +
                '}';
    }
}
