package com.winhearts.arappmarket.download;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.db.DatabaseAccessor;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.PackageUtils;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.io.File;
import java.util.ArrayList;

public class DownloadManager {

    // -1:安装失败 ；0：未下载；1:正在下载；2：暂停；3：下载完成；4：安装完成；5：下载失败;6：等待;7：任务创建失败；8：任务创建中；9：任务创建过程被终止
    public static final int INSTALL_FAIL = -1;
    public static final int UN_DOWN_LOAD = 0;
    public static final int LOADING = 1;
    public static final int PAUSE = 2;
    public static final int COMPLETE_DOWN_LOAD = 3;
    public static final int COMPLETE_INSTALL = 4;
    public static final int DOWN_LOAD_FAIL = 5;
    public static final int WAIT = 6;
    public static final int CREATE_TASK_FAIL = 7;
    public static final int CREATE_TASK = 8;
    public static final int CANCEL_TASK = 9;

//	public  static SQLiteDatabase   mSQLiteDatabase;

    public static void pause(Context mContext, String downloadUrl,
                             String packageName) {
        Intent intent = new Intent(DisplayConfig.ACTION_CONTROL_MESSAGE);
        intent.putExtra("control", PAUSE);
        intent.putExtra("path", downloadUrl);
        intent.putExtra("packageName", packageName);
        mContext.sendBroadcast(intent);
    }

    public static void start(Context mContext, String downloadUrl,
                             String packageName) {
        Intent intent = new Intent(DisplayConfig.ACTION_CONTROL_MESSAGE);
        intent.putExtra("control", LOADING);
        intent.putExtra("path", downloadUrl);
        intent.putExtra("packageName", packageName);
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取不同下载状态中的应用
     *
     * @return 下载任务中的应用列表
     */
    public static ArrayList<DownRecordInfo> getUnCompleteAppList(Context context) {

        ArrayList<DownRecordInfo> loadingList = getDownLoadRecord(DownloadManager.LOADING, context);
        ArrayList<DownRecordInfo> pauseList = getDownLoadRecord(DownloadManager.PAUSE, context);
        ArrayList<DownRecordInfo> waitList = getDownLoadRecord(DownloadManager.WAIT, context);
        ArrayList<DownRecordInfo> createList = getDownLoadRecord(DownloadManager.CREATE_TASK, context);
        loadingList.addAll(pauseList);
        loadingList.addAll(waitList);
        loadingList.addAll(createList);
        return loadingList;
    }

    /**
     * 获取指定状态的下载记录
     **/

    public static ArrayList<DownRecordInfo> getDownLoadRecord(int state, Context mContext) {
        SQLiteDatabase mSQLiteDatabase = DatabaseAccessor.getInstance(mContext).getDB();
        ArrayList<DownRecordInfo> recordList = BasicDataInfo.getAllRecord(mSQLiteDatabase);
        ArrayList<DownRecordInfo> resultList = new ArrayList<>();
        for (int index = 0; index < recordList.size(); index++) {

            DownRecordInfo downRecordInfo = recordList.get(index);
            if (state == downRecordInfo.getState()) {
                resultList.add(downRecordInfo);
            }
        }
        return resultList;

    }

    /**
     * 删除下载记录，和更新下载列表
     *
     * @param mContext
     * @param recordInfo
     */
    public static void appRecordAndFileDelete(Context mContext, DownRecordInfo recordInfo) {
        deleteAppRecordAndFile(mContext, recordInfo);
        VpnStoreApplication.updateRecordList();
    }

    /**
     * 删除下载记录
     *
     * @param mContext
     * @param recordInfo
     */
    public static void deleteAppRecordAndFile(Context mContext, DownRecordInfo recordInfo) {
        BasicDataInfo.deleteDownRecord(VpnStoreApplication.mSQLiteDatabase, recordInfo.getDownloadUrl());
        BasicDataInfo.DeleteRecord(VpnStoreApplication.mSQLiteDatabase, recordInfo.getDownloadUrl());
        DownloadManager.cancel(mContext, recordInfo.getDownloadUrl(), recordInfo.getPackageName());
        DownloadManager.delete(mContext, recordInfo.getAppName(), recordInfo.getDownloadUrl(),
                recordInfo.getPackageName());
    }

    /**
     * 取消下载，删除下载记录
     *
     * @param mContext
     * @param downloadUrl
     * @param packageName
     */
    public static void cancel(Context mContext, String downloadUrl,
                              String packageName) {
        Intent intent = new Intent(DisplayConfig.ACTION_CONTROL_MESSAGE);
        intent.putExtra("control", 4);
        intent.putExtra("path", downloadUrl);
        intent.putExtra("packageName", packageName);
        mContext.sendBroadcast(intent);
    }

    public static void restart(Context mContext, String downloadUrl,
                               String packageName) {
        /*
         * Toast.makeText(mContext, "文件丢失，重新下载！", Toast.LENGTH_LONG).show();
		 */
        /*
         * BasicDataInfo.updateDownRecordState(mSQLiteDatabase,
		 * downRecordInfo.getDownloadUrl(), 8);
		 */
        Intent intent = new Intent(DisplayConfig.ACTION_CONTROL_MESSAGE);
        intent.putExtra("control", CREATE_TASK);
        intent.putExtra("path", downloadUrl);
        intent.putExtra("packageName", packageName);
        mContext.sendBroadcast(intent);
    }


    /**
     * 纯粹的物理删除
     *
     * @param mContext
     * @param appName
     * @param downloadUrl
     * @param packageName
     */
    public static void delete(Context mContext, String appName,
                              String downloadUrl, String packageName) {
//		mSQLiteDatabase = DatabaseAccessor.getInstance(mContext).getDB();
        String str = "/" + appName;
        String fileName = "";
        if (StorageUtils.isExternalStorageWritable()) {
            fileName = DisplayConfig.APK_SAVE_DIR + str;
        }
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    public static void install(Context mContext, String appName,
                               String downloadUrl, String packageName) {

        SQLiteDatabase mSQLiteDatabase = DatabaseAccessor.getInstance(mContext).getDB();

        String str = "/" + appName;
        String fileName = "";
        if (StorageUtils.isExternalStorageWritable()) {
            fileName = DisplayConfig.APK_SAVE_DIR + str;
        }
        File file = new File(fileName);
        LoggerUtil.d("tag", file.toString());
        if (file.exists()) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo apkInfo = pm.getPackageArchiveInfo(
                    fileName, PackageManager.GET_ACTIVITIES);
            if (apkInfo == null) {
                Toast.makeText(mContext, "文件解析错误，重新下载！",
                        Toast.LENGTH_LONG).show();
                BasicDataInfo
                        .updateDownRecordState(mSQLiteDatabase,
                                downloadUrl, 8);
                Intent intent = new Intent(
                        DisplayConfig.ACTION_CONTROL_MESSAGE);
                intent.putExtra("control", 8);
                intent.putExtra("path",
                        downloadUrl);
                // intent.putExtra("appId", appDetail.getId());
                intent.putExtra("packageName",
                        packageName);
                mContext.sendBroadcast(intent);
            } else {
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setDataAndType(
//						Uri.fromFile(new File(fileName)),
//						"application/vnd.android.package-archive");
//				mContext.startActivity(intent);
                int rval = PackageUtils.install(mContext, fileName, packageName);
                if (rval == PackageUtils.INSTALL_FAILED_INVALID_URI) {
                    ToastUtils.show(mContext, appName + "安装失败");
                }
            }
        } else {
            Toast.makeText(mContext, "文件丢失，重新下载！",
                    Toast.LENGTH_LONG).show();
            BasicDataInfo.updateDownRecordState(mSQLiteDatabase,
                    downloadUrl, 8);
            Intent intent = new Intent(
                    DisplayConfig.ACTION_CONTROL_MESSAGE);
            intent.putExtra("control", 8);
            intent.putExtra("path", downloadUrl);
            intent.putExtra("packageName",
                    packageName);
            mContext.sendBroadcast(intent);
        }
        //这边不能关闭数据库，不然会出现数据库操作异常
//		mSQLiteDatabase.close();
    }

}
