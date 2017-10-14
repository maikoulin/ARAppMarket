package com.winhearts.arappmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.winhearts.arappmarket.service.PollingService;

/**
 * 监听开机启动广播,启动Service
 *
 * @author hedy
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    private final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        // zdf:守护进程启动PollingService
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("andriod.intent.action.VpnRestartService")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //这步主要是为了防止心跳服务没有起来。其实在Application里是已经调用了一次
                    Intent intent1 = new Intent(context, PollingService.class);
                    context.startService(intent1);
                }
            }, 5000);

        }
    }

}
