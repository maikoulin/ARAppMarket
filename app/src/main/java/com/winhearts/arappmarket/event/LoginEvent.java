package com.winhearts.arappmarket.event;

/**
 * rxjava消息传递，用于登录模块
 */
public class LoginEvent {

//	public static final int FROM_NORMAL = 0;
//	public static final int FROM_PAY = 1;

    private String winId;
    private String loginToken;
    private int direct;
    private boolean isError = false;
    private AccountError error = null;

    public class AccountError {
        public int code = 0;
        public String msg;

        public AccountError(int code, String msg) {
            // TODO Auto-generated constructor stub
            this.code = code;
            this.msg = msg;
        }


        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "AccountError [code=" + code + ", msg=" + msg + "]";
        }

    }

//	public LoginEvent(int code, boolean error, String errorMsg){
//		this.isError = error;
//	}

    public LoginEvent(String winId, String loginToken, int code) {
        // TODO Auto-generated constructor stub
        this.winId = winId;
        this.loginToken = loginToken;
        this.direct = code;
        this.error = null;
    }

    public LoginEvent(String winId, String loginToken, int code, int errorCode, String errorMsg) {
        // TODO Auto-generated constructor stub
        this.winId = winId;
        this.loginToken = loginToken;
        this.direct = code;
        this.error = new AccountError(errorCode, errorMsg);
    }

    public String getwinId() {
        return winId;
    }

    public void setwinId(String winId) {
        this.winId = winId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }


    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }


    public AccountError getError() {
        return error;
    }

    public void setError(AccountError error) {
        this.error = error;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    @Override
    public String toString() {
        return "LoginEvent [winId=" + winId + ", loginToken=" + loginToken + ", direct=" + direct + ", isError="
                + isError + ", error=" + error + "]";
    }


}
