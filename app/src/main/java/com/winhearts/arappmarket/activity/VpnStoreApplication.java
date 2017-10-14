
package com.winhearts.arappmarket.activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.db.DatabaseAccessor;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.service.DownloadService;
import com.winhearts.arappmarket.service.PollingService;
import com.winhearts.arappmarket.utils.BitmapUtil;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.cust.AppmarketCrashHandler;

import java.util.ArrayList;

/**
 * Application
 * 设置全局的初始化
 */
public class VpnStoreApplication extends Application {
    public static ArrayList<DownRecordInfo> recordList = new ArrayList<>();
    private Context mContext;
    private DatabaseAccessor database;
    public static SQLiteDatabase mSQLiteDatabase;
    public static VpnStoreApplication app;
    public static boolean hasQuery = false;
    private String mLayoutCode;

//    public static RefWatcher getRefWatcher(Context context) {
//        VpnStoreApplication application = (VpnStoreApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    //
//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mContext = this;
//        refWatcher = LeakCanary.install(this);
        initMainProcess();
        initComProcess();
    }

    private void setFrescoConfig() {
        ImagePipelineConfig config;
        if (BitmapUtil.getCompress() < 200) {
            DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(mContext)
                    .setMaxCacheSize(60 * ByteConstants.MB).build();
            config = ImagePipelineConfig.newBuilder(this)
                    .setBitmapsConfig(Bitmap.Config.RGB_565)
                    .setMainDiskCacheConfig(diskCacheConfig)
                    .setDownsampleEnabled(true)
                    .build();
        } else {
            config = ImagePipelineConfig.newBuilder(this)
                    .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                    .setDownsampleEnabled(true)
                    .build();
        }
        Fresco.initialize(this, config);
    }

    private void initComProcess() {
        initUncaughtExceptionHandler();
    }

    private void initMainProcess() {

        Intent intent = new Intent(this, PollingService.class);
        this.startService(intent);
        mSQLiteDatabase = getSQLDatabase();
        updateRecordList();
        // 注册广播
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(DisplayConfig.ACTION_UPDATE_RECORD);
        registerReceiver(myUpdateRecordBroadCast, myFilter);

        Intent downloadService = new Intent(this, DownloadService.class);
        this.startService(downloadService);
        setFrescoConfig();
    }

    /**
     * 重定向异常栈
     */
    private void initUncaughtExceptionHandler() {
        //默认异常堆栈修改，指定到有输出的地方
        AppmarketCrashHandler handler = AppmarketCrashHandler.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        LoggerUtil.d("VpnStoreApplication", "application start -2");
    }

    private BroadcastReceiver myUpdateRecordBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            updateRecordList();
            Intent intent1 = new Intent(DisplayConfig.ACTION_RESULT_MESSAGE);
            intent.putExtra("fileSize", intent.getIntExtra("fileSize", 0));
            intent.putExtra("downloadSize", intent.getIntExtra("downloadSize", 0));
            intent.putExtra("state", intent.getIntExtra("state", -1));
            intent.putExtra("speed", intent.getIntExtra("speed", -1));
            intent.putExtra("packageName", intent.getStringExtra("packageName"));
            sendBroadcast(intent1);
        }

    };

    public synchronized static void updateRecordList() {
        recordList = BasicDataInfo.getAllRecord(mSQLiteDatabase);
    }

    /**
     * 获取数据库操作对象
     *
     * @return
     */
    public SQLiteDatabase getSQLDatabase() {
        if (database == null) {
            database = DatabaseAccessor.getInstance(this);     // 获取数据库实例
        }
        return database.getDB();
    }

    public static VpnStoreApplication getApp() {
        return app;
    }

    public String getLayoutCode() {
        return mLayoutCode;
    }

    public void setLayoutCode(String layoutCode) {
        mLayoutCode = layoutCode;
    }

}