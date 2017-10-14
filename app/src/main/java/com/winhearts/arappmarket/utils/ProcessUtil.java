package com.winhearts.arappmarket.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by lmh on 2016/3/16.
 */
public class ProcessUtil {
    private static String TAG = "process";

    public static String getCurProcessName(Context context) {
//        int pid = android.os.Process.myPid();
//        ActivityManager mActivityManager = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
//                .getRunningAppProcesses()) {
//            if (appProcess.pid == pid) {
//
//                LogDebugUtil.d(TAG, "current process pid = " + pid + "  name = " + appProcess.processName);
//                return appProcess.processName;
//            }
//        }
//        return null;


        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
