package com.winhearts.arappmarket.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.model.LogInfos;
import com.winhearts.arappmarket.modellevel.ModeLevelIcs;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.FileUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 错误log上报
 */
public class PollingLogReportLogic {
    private final static String TAG = "polling";
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Context mContext;
    private int mTimerInterval = 0;

    public PollingLogReportLogic(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (0 == msg.what) {
                logReport();
            }

        }
    };


    public void startTimer() {

        String timeString = Pref.getString(Pref.LOG_REPORT_CYCLE, mContext, "0");
        //return的情况下需要把mTimerInterval = 0；
        if (TextUtils.isEmpty(timeString)) {
            cancelTimer();
            mTimerInterval = 0;
            return;
        }
        int timeInterval = Integer.parseInt(timeString);
        // 没有周期参数没有轮询，原来有的话终止
        if (timeInterval <= 0) {
            cancelTimer();
            mTimerInterval = 0;
            return;
        }

        // 周期一样不处理
        if (timeInterval == mTimerInterval) {

            return;
        }

        // 时间更改，重启定时器
        cancelTimer();
        mTimerInterval = timeInterval;

        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = Message.obtain();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        };

        mTimer.schedule(mTimerTask, 100, timeInterval * 1000);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

    }

    /**
     * 上报log.(注意，app有自己的上报log sdk也有自己的上报log)
     */
    public static void logReport() {
        LogInfos logInfos = FileUtil.getLogInfos(VpnStoreApplication.getApp(), LoggerUtil.LogPath);
        if (logInfos != null && logInfos.getLogList() != null && logInfos.getLogList().size() > 0) {

            ModeLevelIcs.logReport(VpnStoreApplication.getApp(), logInfos, new ModeUser<String>() {

                @Override
                public void onRequestFail(Throwable e) {
                }

                @Override
                public void onJsonSuccess(String t) {
                    FileUtil.clearFile(LoggerUtil.LogPath);
                }
            });
        }
    }
}
