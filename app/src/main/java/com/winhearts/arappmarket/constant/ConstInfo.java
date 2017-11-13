package com.winhearts.arappmarket.constant;


import android.content.Context;

import com.google.gson.Gson;
import com.winhearts.arappmarket.model.AccountUserInfo;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

/**
 * 常用的帐号信息操作
 */
public class ConstInfo {

    public static String accountWinId;
    public static String accountTokenId;
    public static AccountUserInfo accountInfo;

    public static void setAccountInfo2Pref(Context context, AccountUserInfo info) {
        accountInfo = info;
        PrefNormalUtils.putString(context, PrefNormalUtils.ACCOUNT_INFO, new Gson().toJson(info));
    }

    public static AccountUserInfo updateAccountInfo7Pref(Context context) {
        accountInfo = null;
        String infoString = PrefNormalUtils.getString(context, PrefNormalUtils.ACCOUNT_INFO, "{}");

        Gson gson = new Gson();
        AccountUserInfo info = gson.fromJson(infoString, AccountUserInfo.class);
        accountInfo = info;
        return info;

    }

    public static void update(Context context) {
        updateAccountId7Pref(context);
        updateAccountInfo7Pref(context);
    }

    public static void setAccountId2Pref(Context context, String winId, String tokenId) {
        accountWinId = winId;
        accountTokenId = tokenId;
        PrefNormalUtils.putString(context, PrefNormalUtils.ACCOUNT_TOKEN, tokenId);
        PrefNormalUtils.putString(context, PrefNormalUtils.ACCOUNT_WinID, winId);
    }

    public static void updateAccountId7Pref(Context context) {
        accountWinId = null;
        accountTokenId = null;
        accountWinId = PrefNormalUtils.getString(context, PrefNormalUtils.ACCOUNT_WinID, "");
        accountTokenId = PrefNormalUtils.getString(context, PrefNormalUtils.ACCOUNT_TOKEN, "");
    }
}
