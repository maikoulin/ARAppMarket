package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.event.LoginEvent;
import com.winhearts.arappmarket.model.AccountIDToken;
import com.winhearts.arappmarket.model.AccountUserInfo;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.RxBus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 账号系统
 *
 * @author liw
 */
public class ModeLevelAccount {
    private static final String TAG = "ModeLevelAccount";

    /**
     * 注册
     *
     * @param context
     * @param accountId    账号
     * @param password     密码
     * @param securityCode 验证码
     * @param user
     */
    static public void submit(final Context context, String accountId, String password, String securityCode,
                              final ModeUserErrorCode<AccountIDToken> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_SUBMIT);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", accountId);
        map.put("code", securityCode);
        map.put("password", password);
        map.put("mac", MacUtil.getMacAddress());
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<AccountIDToken>() {
        }.getType();
        SubVolleyResponseHandler<AccountIDToken> subVolleyResponseHandler = new SubVolleyResponseHandler<AccountIDToken>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<AccountIDToken>() {
            @Override
            public void onDataChanged(AccountIDToken data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data.toString());
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
                LogDebugUtil.d(TAG, errorMessage.getMessage());
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 获取验证码
     *
     * @param context
     * @param phoneNum 手机号码
     * @param user
     */
    static public void getSecurityCode(final Context context, String typeString, String phoneNum, final ModeUserErrorCode<String> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_GET_SUBMIT_SECURITY_CODE);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", phoneNum);
        map.put("type", typeString);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data);
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
                LogDebugUtil.d(TAG, errorMessage.toString());
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 检查登陆状态
     *
     * @param context
     * @param tokenId
     * @param user
     */
    static public void checkLoginStatue(final Context context, final int direct, String tokenId, final ModeUser<Map<String, String>> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_CHECK_LOGIN_STATUS);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("loginToken", tokenId);
        map.put("mac", MacUtil.getMacAddress());
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        final SubVolleyResponseHandler<Map<String, String>> subVolleyResponseHandler = new SubVolleyResponseHandler<Map<String, String>>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Map<String, String>>() {
            @Override
            public void onDataChanged(Map<String, String> data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data.toString());
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 账号注销
     *
     * @param context
     * @param winId
     * @param user
     */
    static public void exit(final Context context, String winId, String loginToken, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_EXIT);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("winId", winId);
        map.put("loginToken", loginToken);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data);
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
                LogDebugUtil.d(TAG, errorMessage.toString());
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 登录
     *
     * @param context
     * @param phone
     * @param password
     * @param user
     */
    static public void login(final Context context, String phone, String password, final int direct,
                             final ModeUserErrorCode<AccountIDToken> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_LOGIN);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", phone);
        map.put("password", password);
        map.put("mac", MacUtil.getMacAddress());
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<AccountIDToken>() {
        }.getType();
        SubVolleyResponseHandler<AccountIDToken> subVolleyResponseHandler = new SubVolleyResponseHandler<AccountIDToken>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<AccountIDToken>() {
            @Override
            public void onDataChanged(AccountIDToken data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                    postLoinEvent(new LoginEvent(data.getWinId(), data.getLoginToken(), direct));
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);

                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }


    private static void postLoinEvent(LoginEvent event) {
        RxBus.getDefault().post(event);
    }

    /**
     * 获取登录用户信息
     *
     * @param context
     * @param winId
     * @param loginToken
     * @param user
     */
    static public void getUserInfo(final Context context, String winId, String loginToken,
                                   final ModeUser<AccountUserInfo> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_GET_USER_INFO);

        HashMap<String, String> params = new HashMap<>();
        params.put("winId", winId);
        params.put("loginToken", loginToken);
        Type type = new TypeToken<AccountUserInfo>() {
        }.getType();
        SubVolleyResponseHandler<AccountUserInfo> subVolleyResponseHandler = new SubVolleyResponseHandler<AccountUserInfo>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<AccountUserInfo>() {
            @Override
            public void onDataChanged(AccountUserInfo data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
                if (data != null && !TextUtils.isEmpty(data.getwinId())) {
                    ConstInfo.setAccountInfo2Pref(context, data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
                LogDebugUtil.d(TAG, errorMessage.toString());
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param context
     * @param phone
     * @param email
     * @param nickName
     * @param winId
     * @param loginToken
     * @param user
     */
    static public void updateUserInfo(final Context context, String phone, String email, String nickName, String winId,
                                      String loginToken, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.ACCOUNT_UPDATE_USER_INFO);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", phone);
        map.put("email", email);
        map.put("nickName", nickName);
        map.put("loginToken", loginToken);
        map.put("winId", winId);
        map.put("loginToken", loginToken);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data);
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
                LogDebugUtil.d(TAG, errorMessage.toString());
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    static public void bindPhone(final Context context, String phoneNum, String password, String securityCode,
                                 final ModeUserErrorCode<String> user) {
        final String url = Util.getUrl(context, ModeUrl.BIND_PHONE);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", phoneNum);
        map.put("code", securityCode);
        map.put("password", password);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        params.put("winId", ConstInfo.accountWinId);
        params.put("loginToken", ConstInfo.accountTokenId);
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }

                LogDebugUtil.d(TAG, data);
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }


}
