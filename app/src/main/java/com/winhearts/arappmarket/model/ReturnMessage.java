package com.winhearts.arappmarket.model;

/**
 * 服务器返回数据统一格式
 */
public class ReturnMessage {

    private int returnCode;

    private String returnMsg;

//	private CheckConfig content;

    private String content;

    private int type;

    public static final int ILLEGALITY = -100;
    public static final int SUCCESS = 1;
    public static final int TIME_OFF = -101;
    public static final int PARAM_ERROR = 0;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
