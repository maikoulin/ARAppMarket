package com.winhearts.arappmarket.utils.cust;


import android.content.Context;
import android.util.Log;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.utils.ActivityStack;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.StorageUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;


/**
 * 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口
 */
public class AppmarketCrashHandler implements UncaughtExceptionHandler {

    private static AppmarketCrashHandler crashHandler;


    private AppmarketCrashHandler() {
    }

    public static synchronized AppmarketCrashHandler getInstance() {
        if (crashHandler != null) {
            return crashHandler;
        } else {
            crashHandler = new AppmarketCrashHandler();
            return crashHandler;
        }
    }

    public void init(Context context) {
    }

    //发生异常时，需要把activity栈清空。
    public void uncaughtException(Thread arg0, Throwable throwable) {
        if (StorageUtils.isExternalStorageWritable()) {
            LoggerUtil.f("应用异常退出", throwable);
        } else {
            LoggerUtil.f("应用异常退出", throwable);
            File file = new File(VpnStoreApplication.getApp().getFilesDir() + "/error.txt");
            try {
                FileUtils.write(file, Log.getStackTraceString(throwable));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ActivityStack.getActivityStack().clearActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
