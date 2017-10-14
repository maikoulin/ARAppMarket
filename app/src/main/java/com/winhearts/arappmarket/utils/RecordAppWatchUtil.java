package com.winhearts.arappmarket.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.model.LogInfo;
import com.winhearts.arappmarket.model.LogInfos;
import com.winhearts.arappmarket.modellevel.ModeLevelIcs;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.common.StorageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.winhearts.arappmarket.utils.common.StorageUtils.isExternalStorageReadable;

/**
 * Created by lmh on 2017/7/17.
 */

public class RecordAppWatchUtil {
    private static final String TAG = "RecordAppWatchUtil";
    private Context mContext;
    private String mPackageName = "com.winhearts.appwatch";
    private StringBuilder mResults = new StringBuilder();
    private int mAppwatchVersion;

    public RecordAppWatchUtil(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        recordAppWactch();
        handler.sendEmptyMessageDelayed(1, 60 * 60 * 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            recordAppWactch();
            handler.sendEmptyMessageDelayed(1, 10 * 60 * 1000);
        }
    };

    public void recordAppWactch() {
        if (isAppInstalled(mContext, mPackageName)) {
            mResults.append("\n内存：" + StorageUtils.getAvailableMemorySize());
            mResults.append("\n运行内存：" + StorageUtils.getFreeMemory(mContext));
            CmdUtil.runtimeExec("ps", mPackageName, cmdResultListenter);
        } else {
            LoggerUtil.d(TAG, "appwatch没有安装: ");
            mResults.append("\n内存：" + StorageUtils.getAvailableMemorySize());
            mResults.append("\n运行内存：" + StorageUtils.getFreeMemory(mContext));
            CmdUtil.runtimeExec("ps", mPackageName, cmdResultListenter);
        }
    }

    private void uploadLog() {
        LogInfos logInfos = new LogInfos();
        logInfos.setProdCode(mContext.getPackageName());
        logInfos.setAppVer(Util.getVersionName(mContext));
        logInfos.setClientCode(MacUtil.getMacAddress());
        logInfos.setClientType(android.os.Build.MODEL);
        logInfos.setClientOsVer("android:" + android.os.Build.VERSION.RELEASE);
        List<LogInfo> logInfoArrayList = getLogInfos();

        if (logInfoArrayList == null) {
            logInfoArrayList = new ArrayList<>();

            LogInfo logInfo = new LogInfo();
            logInfo.setLevel("ERROR");
            logInfo.setTitle("appwatch监听");
            logInfo.setContent("appwatch 没有日志或暂无日志");
            logInfo.setCreateTime("" + Util.getAmendCurrentTimeMs(Util.getUrl(VpnStoreApplication.getApp(), "")));
            logInfoArrayList.add(logInfo);
        }
        LogInfo logInfo = new LogInfo();
        logInfo.setLevel("ERROR");
        logInfo.setTitle("appwatch监听");
        logInfo.setContent(mResults.toString());
        logInfo.setCreateTime("" + Util.getAmendCurrentTimeMs(Util.getUrl(VpnStoreApplication.getApp(), "")));
        logInfoArrayList.add(logInfo);

        logInfos.setLogList(logInfoArrayList);


        ModeLevelIcs.logReport(mContext, logInfos, new ModeUser<String>() {
            @Override
            public void onJsonSuccess(String s) {
                Log.d(TAG, "onJsonSuccess: ");
                clearLogFile();
            }

            @Override
            public void onRequestFail(Throwable e) {
                e.printStackTrace();
                Log.d(TAG, "onRequestFail: " + LoggerUtil.getStackTraceString(e));

            }
        });
    }

    CmdUtil.CmdResultListenter cmdResultListenter3 = new CmdUtil.CmdResultListenter() {

        @Override
        public void setResult(String result) {

            if (result == null) {
                LoggerUtil.d(TAG, "散列数据 == null");
                mResults.append("\n");
                mResults.append("散列数据 null");
            } else {
                LoggerUtil.d(TAG, "result:" + result);
                mResults.append("\n");
                mResults.append("散列数据 :");
                mResults.append(result);
            }
            uploadLog();
        }
    };

    CmdUtil.CmdResultListenter cmdResultListenter2 = new CmdUtil.CmdResultListenter() {

        @Override
        public void setResult(String result) {

            if (result == null) {
                LoggerUtil.d(TAG, "result == null");
                mResults.append("\n");
                mResults.append("result == null");
            } else {
                LoggerUtil.d(TAG, "result:" + result);
                mResults.append("\n");
                mResults.append("/system/app :");
                mResults.append(result);
            }

            try {
                Runtime.getRuntime().exec(new String[]{"su"});
                CmdUtil.runtimeExec(new String[]{"/system/bin/chmod", "777", "/data/data/com.winhearts.appwatch/files/chinanetcenter_appwatch.log_local"}, "", new CmdUtil.CmdResultListenter() {
                    @Override
                    public void setResult(String result) {
                        Log.d(TAG, "setResult: "+result);
                    }
                });
                CmdUtil.runtimeExec(new String[]{"/system/bin/cat", "/data/data/com.winhearts.appwatch/files/chinanetcenter_appwatch.log_local"}, "random", cmdResultListenter3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    CmdUtil.CmdResultListenter cmdResultListenter = new CmdUtil.CmdResultListenter() {

        @Override
        public void setResult(String result) {
            if (result == null) {
                LoggerUtil.d(TAG, "appwatch没有启动");
                mResults.append("\n");
                mResults.append("appwatch没有启动");
                tryToStartAppwatch();

                getInfoFromAppMarket();
            } else {
                LoggerUtil.d(TAG, "appwatch有启动");
                mResults.append("\n");
                mResults.append("appwatch已启动 :");
                mResults.append(result);

                getInfoFromAppMarket();
            }
        }
    };

    private void tryToStartAppwatch() {
        try {

            Intent intent = new Intent();
            intent.setAction("com.winhearts.vpnService");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            mResults = new StringBuilder("appwatch没有安装");
            return false;
        } else {
            mResults = new StringBuilder("appwatch已安装");
            mResults.append("\nappwatch version:");
            mAppwatchVersion = packageInfo.versionCode;
            mResults.append(packageInfo.versionCode);
            return true;
        }
    }


    /*日志的格式为
     * "%d{yyyy:MM:dd HH:mm:ss},%p, %m%n(tag,content)"
	 * 日期 ， 级别，标题 ，内容 |
	 */
    private static List<LogInfo> getLogInfos() {

        if (isExternalStorageReadable()) {
            String filePath = Environment.getExternalStorageDirectory()
                    + File.separator + "com.winhearts.appwatch"
                    + File.separator + "chinanetcenter_appwatch.log_local";

            if (filePath != null) {
                File file = new File(filePath);
                FileInputStream inputStream = null;
                if (file.exists()) {
                    try {
                        inputStream = new FileInputStream(file);
                        if (inputStream.available() > 0) {
                            String logContent = inputStream2String(inputStream);
                            String[] logItems = logContent.split("\\|");

                            List<LogInfo> logInfoList = new ArrayList<LogInfo>();
                            for (int i = 0; i < logItems.length - 1; i++) {
//                                Log.d(TAG, "getLogInfos: " + logItems[i]);
                                String logItem = logItems[i];
//                                String[] itemContent = logItem.split(",");
                                LogInfo logInfo = new LogInfo();
                                logInfo.setCreateTime("" + Util.getAmendCurrentTimeMs(Util.getUrl(VpnStoreApplication.getApp(), "")));
                                logInfo.setLevel("INFO");
                                logInfo.setTitle("appwatch监听");
                                logInfo.setContent(logItem);
                                logInfoList.add(logInfo);
                            }


                            return logInfoList;
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void clearLogFile() {
        try {
            String filePath = Environment.getExternalStorageDirectory()
                    + File.separator + "com.winhearts.appwatch"
                    + File.separator + "chinanetcenter_appwatch.log_local";
            File f = new File(filePath);
            FileWriter fw = new FileWriter(f);
            fw.write("");
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String inputStream2String(InputStream is) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (int i; (i = is.read(buf)) != -1; ) {
            baos.write(buf, 0, i);
        }
        String data = baos.toString("UTF-8");
        baos.close();
        return data;
    }


    private void getInfoFromAppMarket() {
        Uri uri = Uri.parse("content://com.winhearts.appwatch.app.provider.ChannelContentProvider/table_channel_info");

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    mResults.append("\nappwatch ContentProvider sVmsUrl:" + cursor.getString(1));
//                    mResults.append("\nappwatch ContentProvider sAmsUrl:" + cursor.getString(2));
//                    mResults.append("\nappwatch ContentProvider sProject:" + cursor.getString(3));
                }
            } else {
                Log.d(TAG, "getInfoFromAppMarket: cursor null");
                mResults.append("\ncursor == null:");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mAppwatchVersion < 15) {
//                mResults.append("\nappwatch 版本号")；
            } else {
//            CmdUtil.runtimeExec(new String[]{"/system/bin/ls", "/system/app"}, "AppWatch", cmdResultListenter2);
            }
            uploadLog();
        }
    }


}
