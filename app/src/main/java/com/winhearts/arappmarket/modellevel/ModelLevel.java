package com.winhearts.arappmarket.modellevel;

import android.text.TextUtils;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.utils.LoggerUtil;

/**
 * Created by lmh on 2017/5/25.
 */

public class ModelLevel {

    public static void setLogMessage(int errorCode, String errorStr, String systemName, String parameter) {
        if (!TextUtils.isEmpty(errorStr) && errorStr.contains("TimeoutError")) {
            LoggerUtil.w(systemName + VpnStoreApplication.getApp().getString(R.string.no_response), parameter + errorStr);
        } else {
            LoggerUtil.w(systemName + VpnStoreApplication.getApp().getString(R.string.failure_in_link), parameter + errorStr);
        }
    }
}
