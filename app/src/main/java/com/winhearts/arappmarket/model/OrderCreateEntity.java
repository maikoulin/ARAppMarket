package com.winhearts.arappmarket.model;

import java.io.Serializable;

/**
 * 订单信息，用于支付模块的创建订单
 */
public class OrderCreateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sellerOrderCode;// :开发商订单号
    private String status;// :订单状态(PAID:已支付;UNPAID未支付)
    private String price;// price:价格
    private String prodName;// 产品名称
    private String prodNum;// 商品数量
    //	private String payTime;// 订单支付时间(unix时间戳，单位ms)
    private String appKey;// :" 12301234512 ",
    private String uid;// :" 12301234512 ",
    private String note;//角色

    public String getSellerOrderCode() {
        return sellerOrderCode;
    }

    public void setSellerOrderCode(String sellerOrderCode) {
        this.sellerOrderCode = sellerOrderCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProName() {
        return prodName;
    }

    public void setProName(String proName) {
        this.prodName = proName;
    }

    public String getProNum() {
        return prodNum;
    }

    public void setProNum(String proNum) {
        this.prodNum = proNum;
    }

//	public String getPayTime() {
//		return payTime;
//	}
//
//	public void setPayTime(String payTime) {
//		this.payTime = payTime;
//	}

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "OrderCreateEntity [sellerOrderCode=" + sellerOrderCode + ", status=" + status + ", price=" + price
                + ", proName=" + prodName + ", proNum=" + prodNum + ", " + "" + ", appKey=" + appKey
                + ", uid=" + uid + ", note=" + note + "]";
    }


}
