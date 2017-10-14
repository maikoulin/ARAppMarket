package com.winhearts.arappmarket.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.winhearts.arappmarket.logic.CheckLoginLogic;
import com.winhearts.arappmarket.logic.PollingLogReportLogic;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAms;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;

import java.util.Timer;
import java.util.TimerTask;

public class PollingService extends Service {
    private final static String TAG = "polling";
    private final static boolean IS_DEBUG = false;
    private Context mContext;
    public static final int QUERY_CONFIG = 4;
    private static Timer timer = null;
    private int period = 0;

    //log上报
    private final ThreadLocal<PollingLogReportLogic> pollingLogReportLogic;

    private boolean oneShoot = false;

    public PollingService() {
        pollingLogReportLogic = new ThreadLocal<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerUtil.d("服务开启", "startService");
        mContext = this;
        start(mContext);
        pollingLogReportLogic.set(new PollingLogReportLogic(this));

        CheckLoginLogic.getInstance().checkLogin(true);
    }

    public void onDestroy() {
        super.onDestroy();
        pollingLogReportLogic.get().cancelTimer();
        stop();
    }

    public void start(final Context context) {
        if (timer == null) {
            timer = new Timer();
            period = Integer.parseInt(Pref.getString(Pref.CONFIG_FETCH_CYCLE, context, "300"));

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = QUERY_CONFIG;
                    httpRequestHandler.sendMessage(msg);
                }
            }, 60000, period * 1000);
        } else {
            LogDebugUtil.d(TAG, "timer != null");
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void queryForce(final Context context) {
        ModeLevelVms.queryConfigForce(context, 2, TAG, new ModeUserErrorCode<ConfigInfo>() {
            @Override
            public void onJsonSuccess(ConfigInfo configInfo) {
                oneShoot = true;
                getConfigSuccess(configInfo);

            }

            @Override
            public void onRequestFail(int errorCode, Throwable e) {
                oneShoot = false;
            }
        });
    }

    public void queryConfig(Context context) {
        ModeLevelVms.queryConfig(context, new ModeUser<ConfigInfo>() {
            @Override
            public void onJsonSuccess(ConfigInfo response) {
                getConfigSuccess(response);
            }

            @Override
            public void onRequestFail(Throwable e) {
                oneShoot = false;
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler httpRequestHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == QUERY_CONFIG) {
                ModeLevelAms.queryAllPackages(mContext, null);
                if (!oneShoot) {
                    queryForce(mContext);
                } else {
                    queryConfig(mContext);
                }

            }
        }
    };

    /**
     * 获取配置做逻辑  保存数据
     *
     * @param response
     */
    private void getConfigSuccess(ConfigInfo response) {
        if (response != null) {
            String showInstall = response.getShow_install_hint();
            Intent installService = new Intent(mContext, InstallHintService.class);
            if (TextUtils.isEmpty(showInstall) || showInstall.equals("0")) {
                mContext.stopService(installService);
            } else {
                mContext.startService(installService);
            }
            String oldCycle = Pref.getString(Pref.CONFIG_FETCH_CYCLE, mContext, "");
            String newCycle = response.getConfig_fetch_cycle();
            ConfigInfo.savaConfig(mContext, response);
            if (!oldCycle.equals(newCycle) || !oldCycle.equals(String.valueOf(period))) {
                stop();
                start(mContext);
            }
        }
        pollingLogReportLogic.get().startTimer();
    }
}
