package com.winhearts.arappmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;

/**
 * 网络状态监听
 *
 * @author hedy
 */
public class NetworkStateMonitorReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateMonitorReceiver";

    private int mRecordNetworkType = -1;

    public int getNetworkType() {
        return mRecordNetworkType;
    }

    public void setNetworkType(int networkType) {
        this.mRecordNetworkType = networkType;
    }

    Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {

        mContext = context;

        if (intent != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            int netWorkType = getNetworkType(context);
            if (netWorkType == -1) {
                return;
            }

            if (netWorkType != mRecordNetworkType) {
                if (VpnStoreApplication.hasQuery) {
                    return;
                }
                VpnStoreApplication.hasQuery = true;
                ModeLevelVms.queryConfigForce(context, 2,TAG, new ModeUserErrorCode<ConfigInfo>() {
                    @Override
                    public void onJsonSuccess(ConfigInfo configInfo) {
                        if (configInfo != null) {
                            ConfigInfo.savaConfig(mContext, configInfo);
                        }
                    }

                    @Override
                    public void onRequestFail(int code, Throwable e) {
                    }
                });
            }
            setNetworkType(netWorkType);
        }
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            if (!info.isAvailable()) {
                return -1;
            } else {
                return 1;
            }
        }
        return -1;
    }
}