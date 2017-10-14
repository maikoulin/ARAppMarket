package com.winhearts.arappmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.winhearts.arappmarket.utils.SoftwareUtil;

/**
 * 系统关闭广播：在系统关闭时更新应用使用记录
 * Created by lmh on 2016/11/14.
 */

public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SoftwareUtil.updateUsedRecord(context);

    }
}
