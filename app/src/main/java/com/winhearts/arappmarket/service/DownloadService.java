package com.winhearts.arappmarket.service;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.db.DatabaseAccessor;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.SmartDownloadProgressListener;
import com.winhearts.arappmarket.download.SmartFileDownloader;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.event.InstallEvent;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.StateInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    public static DownloadService instance;
    private static SQLiteDatabase mSQLiteDatabase;
    private static DatabaseAccessor database;
    private static Context mContext;
    private int loadSum = 0;
    private ArrayList<String> pauseList = new ArrayList<String>();
    private ArrayList<String> loadList = new ArrayList<String>();
    private String apkSaveDir = "";
    private Thread weakThread = new Thread(new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                checkPauseList();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });
    private ArrayList<SmartFileDownloader> list = new ArrayList<SmartFileDownloader>();
    private BroadcastReceiver myBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {

            if (intent.getAction().equals(DisplayConfig.ACTION_CONTROL_MESSAGE)) {
                // final String path = intent.getStringExtra("path");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // synchronized(list)
                        String path = intent.getStringExtra("path");
                        {
                            int index = 0;
                            boolean inList = false;
                            if (intent.getIntExtra("control", 0) == 5) {
                                pauseList.clear();
                                loadSum = 0;
                                // Pref.saveInt(Pref.DOWNLOAD_NUMBER, loadSum,
                                // mContext);
                                return;
                            }
                            for (SmartFileDownloader loader : list) {
                                if (loader.getDownloadUrl().equals(path)) {
                                    inList = true;
                                    switch (intent.getIntExtra("control", 0)) {
                                        case 1: // 下载
                                            if (loader.getFileName().equals("") && loader.getDownloadSize() == 0) {
                                                int appId = intent.getIntExtra("appId", 0);
                                                String packageName = intent.getStringExtra("packageName");
                                                BasicDataInfo.updateDownRecordState(mSQLiteDatabase, path, 8);
                                                Intent intent = new Intent(DisplayConfig.ACTION_ADD_MESSAGE);
                                                intent.putExtra("control", 1);
                                                intent.putExtra("path", path);
                                                intent.putExtra("appId", appId);
                                                intent.putExtra("packageName", packageName);
                                                sendBroadcast(intent);

                                                Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                                                sendBroadcast(intent1);
                                            } else {
                                                loader.setForce(false);
                                                loader.setState(6);
                                                pauseList.add(path);
                                                sendBroadCast(loader.getFileSize(), loader.getDownloadSize(), 6, "等待中", loader.getPackageName());

                                            }
                                            break;
                                        case 2: // 暂停
                                        {
                                            for (int j = 0; j < pauseList.size(); j++) {
                                                String str = pauseList.get(j);
                                                if (str.equals(loader.getDownloadUrl())) {
                                                    pauseList.remove(j);
                                                    break;
                                                }
                                            }
                                            // if(loader.getState() == 6) {
                                            // loader.setState(2);
                                            // } else {
                                            loader.stop();
                                            sendBroadCast(loader.getFileSize(), loader.getDownloadSize(), 2, "0KB/S", loader.getPackageName());
                                            // }
                                            Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                                            sendBroadcast(intent1);
                                        }
                                        break;
                                        case 3: // 获取信息过程中 终止
                                            for (int j = 0; j < pauseList.size(); j++) {
                                                String str = pauseList.get(j);
                                                if (str.equals(loader.getDownloadUrl())) {
                                                    pauseList.remove(j);
                                                    break;
                                                }
                                            }
                                            loader.forceStop();
                                            break;
                                        case 4: // 删除
                                            LogDebugUtil.i(TAG, "删除任务");
                                            for (String str : pauseList) {
                                                if (str.equals(loader.getDownloadUrl())) {
                                                    pauseList.remove(str);
                                                    break;
                                                }
                                            }

                                            loader.stop();
                                            BasicDataInfo.deleteDownRecord(mSQLiteDatabase, loader.getDownloadUrl());
                                            BasicDataInfo.DeleteRecord(mSQLiteDatabase, loader.getDownloadUrl());
                                            removeLoader(loader.getPackageName());
                                            Intent intent = new Intent(DisplayConfig.ACTION_RESULT_MESSAGE);
                                            intent.putExtra("state", DownloadManager.CANCEL_TASK);
                                            sendBroadcast(intent);
                                            break;
                                        case 5: // 完成
                                            break;
                                        case 6: // 允许2G3G下下载
                                            loader.setDownloadIn2g3g(true);
                                            break;
                                        case 7: // 下载失败重试

                                            break;
                                        case 8: // 安装文件丢失（人为删除）时，重新下载
                                        {
                                            File dir;
                                            if (StorageUtils.isExternalStorageWritable()) {
                                                dir = new File(DisplayConfig.APK_SAVE_DIR);// 文件保存目录
                                            } else {
                                                dir = new File(apkSaveDir);// 文件保存目录
                                            }
                                            loader.setForce(false);
                                            loader.tryAgain2(path, dir, mHandler);
                                        }
                                        break;
                                        default:
                                            break;
                                    }
                                    break;
                                }
                                index++;
                            }
                            if (!inList) {
                                int appId = intent.getIntExtra("appId", 0);
                                String packageName = intent.getStringExtra("packageName");

                                if (intent.getIntExtra("control", 0) == 1) {
                                    BasicDataInfo.updateDownRecordState(mSQLiteDatabase, path, 8);
                                    Intent intent = new Intent(DisplayConfig.ACTION_ADD_MESSAGE);
                                    intent.putExtra("control", 1);
                                    intent.putExtra("path", path);
                                    intent.putExtra("appId", appId);
                                    intent.putExtra("packageName", packageName);
                                    sendBroadcast(intent);

                                    Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                                    sendBroadcast(intent1);
                                }
                            }

                        }

                    }

                }).start();
            }
        }

    };
    private BroadcastReceiver installFailBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int appid = intent.getIntExtra("appId", 0);
            String appName = intent.getStringExtra("appName");
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(appid);
        }

    };
    private BroadcastReceiver myAddBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(DisplayConfig.ACTION_ADD_MESSAGE)) {
                // final String path = intent.getStringExtra("path");
                // final int appId = intent.getIntExtra("appId",0);
                // removeLoader(appId);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path = intent.getStringExtra("path");
                        int appId = intent.getIntExtra("appId", 0);
                        String packageName = intent.getStringExtra("packageName");
                        removeLoader(packageName);//清除列表残留的下载对象
                        Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                        sendBroadcast(intent1);
                        if (StorageUtils.isExternalStorageWritable()) {
                            File dir = new File(DisplayConfig.APK_SAVE_DIR);// 文件保存目录
                            SmartFileDownloader loader = new SmartFileDownloader(mContext, path, dir, 1, packageName);
                            list.add(loader);
                            list.get(list.size() - 1).getApkMsg(dir, mHandler);
                        } else {
                            File dir = new File(apkSaveDir);// 文件保存目录
                            SmartFileDownloader loader = new SmartFileDownloader(mContext, path, dir, 1, packageName);
                            list.add(loader);
                            list.get(list.size() - 1).getApkMsg(dir, mHandler);
                        }

                    }
                }).start();
            }
        }

    };

    private void removeLoader(String packageName) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPackageName() != null && list.get(i).getPackageName().equals(packageName)) {
                list.get(i).clear();
                if (list.size() > i) {
                    list.remove(i);
                }
                break;
            }
        }
    }

    private SmartFileDownloader getLoader(String packageName) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPackageName() != null) {
                if (list.get(i).getPackageName().equals(packageName)) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DownloadService> mService;

        MyHandler(DownloadService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadService service = mService.get();
            if (service != null) {
                StateInfo info = (StateInfo) msg.obj;

                SmartFileDownloader loader = service.getLoader(info.getPackageName());
                if (loader != null) {
                    Intent intent = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                    service.sendBroadcast(intent);
                    if (loader.getState() == 5 || loader.getState() == 7) {
                        service.removeLoader(info.getPackageName());
                        service.sendBroadCast(info.getFileSize(), info.getDownloadSize(), info.getState(), "0KB/S", info.getPackageName());
                        return;
                    }
                    if (loader.getState() == 1) {
                        loader.setState(6);
                        service.pauseList.add(loader.getDownloadUrl());
                        service.sendBroadCast(loader.getFileSize(), loader.getDownloadSize(), 6, "等待中", info.getPackageName());
                    }
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    private void checkPauseList() {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getState() == 1) {
                sum++;
            }
        }
        loadSum = sum;
        if (loadSum < 2 && pauseList.size() > 0) {
            for (SmartFileDownloader loader : list) {
                if (loader.getDownloadUrl().equals(pauseList.get(0))) {
                    try {
                        loadSum++;
                        pauseList.remove(0);
                        loader.download(new SmartDownloadProgressListener() {
                            @Override
                            public void onDownloadSize(int size, int fileSize, int state, String speed, String packageName) {
                                // 可以实时得到文件下载的长度
                                sendBroadCast(fileSize, size, state, speed, packageName);
                                if (state == 5 || state == 3 || state == 7) {
                                    loadSum--;
                                    if (loadSum < 0) {
                                        loadSum = 0;
                                    }
                                }
                            }
                        });
                        break;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        if (loadSum <= 0) {
            stopForeground(true);
        }

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        String action;

        if (intent == null) {
            action = DisplayConfig.ACTION_START_SERVICE;
        } else {
            action = intent.getAction();
        }

        if (action == null) {
            action = DisplayConfig.ACTION_START_SERVICE;
        }
        if (action.equals(DisplayConfig.ACTION_START_SERVICE)) {

        } else {

        }
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        instance = this;
        mContext = this;
        apkSaveDir = this.getFilesDir().getPath();
        if (database == null) {
            database = DatabaseAccessor.getInstance(this); // 获取数据库实例
            mSQLiteDatabase = database.getDB();
        }
        // Pref.saveInt(Pref.DOWNLOAD_NUMBER, 0, mContext);
        // 注册广播
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(DisplayConfig.ACTION_CONTROL_MESSAGE);
        // myFilter.addAction(ACTION_ADD_MESSAGE);
        // myFilter.addAction(ACTION_DEL_MESSAGE);
        this.registerReceiver(myBroadCast, myFilter);
        // 注册广播
        IntentFilter myFilter1 = new IntentFilter();
        myFilter1.addAction(DisplayConfig.ACTION_ADD_MESSAGE);
        // myFilter.addAction(ACTION_DEL_MESSAGE);
        this.registerReceiver(myAddBroadCast, myFilter1);
        // 注册广播
        IntentFilter myFilter2 = new IntentFilter();
        myFilter2.addAction("install app fail!");
        // myFilter.addAction(ACTION_DEL_MESSAGE);
        this.registerReceiver(installFailBroadCast, myFilter2);

//        AppInstallReceiver.getInstant().registerAppInstallReceiver(this);
        ArrayList<DownRecordInfo> recordList = BasicDataInfo.getAllRecord(mSQLiteDatabase);
        for (DownRecordInfo info : recordList) {
            if (info.getState() != 5 && info.getState() != 7 && info.getState() != 8 && info.getState() != 9) {
                if (StorageUtils.isExternalStorageWritable()) {
                    File dir = new File(DisplayConfig.APK_SAVE_DIR);// 文件保存目录
                    SmartFileDownloader loader = new SmartFileDownloader(mContext, info.getDownloadUrl(), dir, 1, info.getPackageName());
                    list.add(loader);
                    list.get(list.size() - 1).getApkMsg(dir, mHandler);
                } else {
                    File dir = new File(apkSaveDir);// 文件保存目录
                    SmartFileDownloader loader = new SmartFileDownloader(mContext, info.getDownloadUrl(), dir, 1, info.getPackageName());
                    list.add(loader);
                    list.get(list.size() - 1).getApkMsg(dir, mHandler);
                }
            } else {
                BasicDataInfo.deleteDownRecord(mSQLiteDatabase, info.getDownloadUrl());
                BasicDataInfo.DeleteRecord(mSQLiteDatabase, info.getDownloadUrl());
//                if (info.getState() == 8) {
//                    BasicDataInfo.updateDownRecordState(mSQLiteDatabase, info.getDownloadUrl(), 9);
//                }
            }
        }
        weakThread.start();
        // 数据库更新广播
        Intent intent = new Intent(DisplayConfig.ACTION_UPDATE_DB);
        sendBroadcast(intent);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(myBroadCast);
        unregisterReceiver(myAddBroadCast);
        unregisterReceiver(installFailBroadCast);
        for (SmartFileDownloader loader : list) {
            Log.v("servicestop", "servicestop");
            loader.stop();
        }
        list.clear();
        instance = null;
        // stopForeground(true);
        super.onDestroy();
    }

    public static DownloadService getInstance() {

        return instance;
    }

    public ArrayList<SmartFileDownloader> getList() {
        return list;
    }

    public void receiverFlash(String packageName, int state1) {
        if (state1 == 3) {
            updateNoOpenApp(packageName, PrefNormalUtils.ALL_NO_OPEN_APP);
            updateNoOpenApp(packageName, PrefNormalUtils.NO_OPEN_APP);
        }
        BasicDataInfo.updateStateBypackageName(mSQLiteDatabase, packageName, state1);
        DownRecordInfo info = BasicDataInfo.getRecordBypackageName(mSQLiteDatabase, packageName);
        Intent intent2 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
        sendBroadcast(intent2);
        if (info == null) {
            return;
        }
        if (state1 == 4) {
            ManagerUtil.recordOpenApps(mContext, packageName);
            recordNoOpenApp(packageName, PrefNormalUtils.ALL_NO_OPEN_APP);
            if (StorageUtils.isExternalStorageWritable()) {
                File file = new File(DisplayConfig.APK_SAVE_DIR + "/" + info.getFileName());
                if (file.exists()) {
                    file.delete();
                }
            }
            SmartFileDownloader loader = getLoader(packageName);
            if (loader != null) {
                RxBus.getDefault().post(new InstallEvent(packageName));
                String topPackageName = ManagerUtil.getTopActivityName(this);
                if (TextUtils.isEmpty(topPackageName) || (!topPackageName.equals(this.getPackageName()))) {
                    PrefNormalUtils.putString(PrefNormalUtils.RECORD_INSTALL_PACKAPG_BACK, packageName);
                }
                recordNoOpenApp(packageName, PrefNormalUtils.NO_OPEN_APP);
                //上传安装成功
                ModeLevelAmsUpload.uploadOperateData(mContext, packageName, null, "INSTALL_SUCCESS", true);
                BasicDataInfo.deleteDownRecord(mSQLiteDatabase, loader.getDownloadUrl());
                BasicDataInfo.DeleteRecord(mSQLiteDatabase, loader.getDownloadUrl());
                removeLoader(loader.getPackageName());
            }
        }
        // 数据库更新广播
        Intent intent = new Intent(DisplayConfig.ACTION_UPDATE_DB);
        sendBroadcast(intent);

        sendBroadCast(info.getFileSize(), info.getDownlength(), state1, "0KB/S", info.getPackageName());
    }

    public static String getRecordPackageBack(Context context) {
        return PrefNormalUtils.getString(context, PrefNormalUtils.RECORD_INSTALL_PACKAPG_BACK, "");
    }

    //从第三方界面返回时用于提示应用打开
    public static void setHint(Context context) {
        String recordPackageName = getRecordPackageBack(context);
        if (!TextUtils.isEmpty(recordPackageName)) {
            Dialog dialog = new Dialog(context, R.style.dialog);
            View view = getHintOpenDialogView(context, dialog, recordPackageName);
            if (view != null) {
                Window window = dialog.getWindow();
//                window.setBackgroundDrawableResource(R.color.dailog_alpha_80);

                dialog.setContentView(view);

                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.gravity = Gravity.CENTER;
//                Resources res = context.getResources();
                lp.width = ScreenUtil.getScreenWidth(context);
                lp.height = ScreenUtil.getScreenHeight(context);
                dialog.getWindow().setAttributes(lp);

                dialog.show();
            }
        }
        clearHint();
    }

    public static View getHintOpenDialogView(final Context context, final Dialog dialog, final String packageName) {
        String appName = AppManager.getAppName(context, packageName);
        if (TextUtils.isEmpty(appName)) {
            LoggerUtil.d("hint", "找不到相关应用");
            return null;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_install_hint, null);

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.sd_dialog_install_hint_icon);
        GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
        Drawable drawable;
        try {
            drawable = AppManager.getAppIcon(context,
                    packageName);
            if (drawable != null) {
                hierarchy.setPlaceholderImage(drawable, ScalingUtils.ScaleType.FIT_XY);
            } else {
                hierarchy.setPlaceholderImage(R.drawable.background_app_icon);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextView tvName = (TextView) view.findViewById(R.id.tv_dialog_install_hint_name);
        tvName.setText(appName);

        TextView tvOpen = (TextView) view.findViewById(R.id.tv_dialog_install_hint_open);
        tvOpen.requestFocus();
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManagerUtil.startApk(context, packageName);
                dialog.dismiss();
            }
        });

        TextView tvCancel = (TextView) view.findViewById(R.id.tv_dialog_install_hint_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return view;
    }

    public static void clearHint() {
        PrefNormalUtils.putString(PrefNormalUtils.RECORD_INSTALL_PACKAPG_BACK, "");
    }

    /**
     * 保存新安装的软件包名
     *
     * @param packageName 安装包名
     * @param key         存储Key
     */
    private void recordNoOpenApp(String packageName, String key) {

        String noOpenApp = PrefNormalUtils.getString(mContext, key, null);
        ArrayList<String> noOpens;
        if (!TextUtils.isEmpty(noOpenApp)) {
            noOpens = new Gson().fromJson(noOpenApp, new TypeToken<ArrayList<String>>() {
            }.getType());
            if (!noOpenApp.contains(packageName)) {
                noOpens.add(packageName);
            }
            //最多指红点显示5个
            if (key.equals(PrefNormalUtils.ALL_NO_OPEN_APP) && noOpens.size() > 5) {
                noOpens.remove(0);
            }
        } else {
            noOpens = new ArrayList<String>();
            noOpens.add(packageName);
        }
        PrefNormalUtils.putString(mContext, key, new Gson().toJson(noOpens));

    }

    /**
     * 删除未打开卸载应用记录
     *
     * @param packageName 安装包名
     * @param key         存储Key
     */
    private void updateNoOpenApp(String packageName, String key) {

        String noOpenApp = PrefNormalUtils.getString(mContext, key, null);
        if (!TextUtils.isEmpty(noOpenApp) && noOpenApp.contains(packageName)) {
            ArrayList<String> noOpens = new Gson().fromJson(noOpenApp, new TypeToken<ArrayList<String>>() {
            }.getType());
            noOpens.remove(packageName);
            PrefNormalUtils.putString(mContext, key, new Gson().toJson(noOpens));
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendBroadCast(int fileSize, int size, int state, String speed, String packageName) {
        Intent intent = new Intent(DisplayConfig.ACTION_RESULT_MESSAGE);
//		intent.putExtra("appId", id);
        intent.putExtra("fileSize", fileSize);
        intent.putExtra("downloadSize", size);
        intent.putExtra("state", state);
        intent.putExtra("speed", speed);
        intent.putExtra("packageName", packageName);
        sendBroadcast(intent);
    }
}
