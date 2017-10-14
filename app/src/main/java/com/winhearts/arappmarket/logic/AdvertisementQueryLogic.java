package com.winhearts.arappmarket.logic;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.winhearts.arappmarket.activity.AdvertisementActivity;
import com.winhearts.arappmarket.model.AdvertisementEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsAd;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 广告查询逻辑
 * Created by lmh on 2015/7/29.
 */
public class AdvertisementQueryLogic {
    private final static String TAG = "adlogic";
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Context mContext;
    private int mTimerInterval = 0;

    public AdvertisementQueryLogic(Context context) {
        this.mContext = context;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                queryAd();
            }
        }
    };


    public void startTimer() {
        String timeString = Pref.getString(Pref.AD_CYCLE, mContext, "0");
        if (TextUtils.isEmpty(timeString)) {
            cancelTimer();
            return;
        }
        int timeInterval = Integer.parseInt(timeString);
        // 没有周期参数没有轮询，原来有的话终止
        if (timeInterval <= 0) {
            cancelTimer();
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
                Message ms = Message.obtain();
                ms.what = 0;
                mHandler.sendMessage(ms);

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

    private void queryAd() {
        ModeLevelAmsAd.queryAd(mContext, new ModeUser<AdvertisementEntity>() {
            @Override
            public void onJsonSuccess(final AdvertisementEntity advertisementEntity) {
                String old_Id = PrefNormalUtils.getString(mContext, PrefNormalUtils.AD_IMAGE_HOME, "null");
                String new_Id = advertisementEntity.getList().get(0).getId();
                if (getScreenState() && !old_Id.equals(new_Id)) {
                    Intent intent = new Intent(mContext, AdvertisementActivity.class);
                    intent.putExtra("advertisement", advertisementEntity);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);


                }
            }

            @Override
            public void onRequestFail(Throwable e) {

            }
        });
    }

    //判断是否是系统桌面
    public boolean getScreenState() {
        boolean isLaunch = false;
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (mContext
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getPackageName();
            LoggerUtil.d(TAG, "  topActivity == " + topActivityClassName);
            isLaunch = topActivityClassName.contains("launcher") || topActivityClassName.contains("cibn") || topActivityClassName.contains("gzportal");
        }
        return isLaunch;
    }
}

