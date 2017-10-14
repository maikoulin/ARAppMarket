package com.winhearts.arappmarket.download;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.RemoteViews;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.db.DatabaseAccessor;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.StateInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.PackageUtils;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件下载器
 *
 * @author lmh
 */
public class SmartFileDownloader implements Serializable {

    /* 下载状态 */
    private int state = 0; // -1:安装失败 0：未下载；1:正在下载；2：暂停；3：下载完成；4：安装完成；5：下载失败;6：等待;7：任务创建失败；8：任务创建中；9：任务创建过程被终止

    private int STATE_COMPLETE = 3;
//	private int 

    private static final long serialVersionUID = 1L;
    private Object lock = new Object();
    private static final String TAG = "SmartFileDownloader";
    private DatabaseAccessor database;
    private SQLiteDatabase mSQLiteDatabase;
    private Context context;
    /* 已下载文件长度 */
    private int downloadSize = 0;
    /* 原始文件长度 */
    private int fileSize = 0;
    /* 文件名称 */
    private String fileName = "";
    /* 线程数 */
    private SmartDownloadThread[] threads;

    private ArrayList<SmartDownloadThread> threadList = new ArrayList<SmartDownloadThread>();
    /* 本地保存文件 */
    private File saveFile;

    private String mFileSaveDir;
    /* 缓存各线程下载的长度 */
    private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
    /* 每条线程下载的长度 */
    private int block;
    /* 下载路径 */
    private String downloadUrl;
    /* id */
//	private int appId;
    private String appName;
    private String packageName;

    private int oldDownloadSize = 0;

    private long current = 0;
    private boolean isLoad = false;

    private DownRecordInfo record;

    private SmartFileDownloader loader;

    private boolean stop = false;

    private RemoteViews remoteViews;// 状态栏通知显示的view
    private boolean isDownloadIn2g3g = false; // 是否允许在2G3G下下载

    private int mThreadNum = 0;
    private HttpURLConnection infoConn;

    private boolean isForce = false;


    /**
     * 获取线程数
     */
    public int getThreadSize() {
        return threads.length;
    }

    /**
     * 获取文件大小
     *
     * @return
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 累计已下载大小
     *
     * @param size
     */
    public synchronized void append(int size) {
        downloadSize += size;
//		print("downloadSize " + "downLength " + downloadSize);
    }

    /**
     * 更新指定线程最后下载的位置
     *
     * @param threadId 线程id
     * @param pos      最后下载的位置
     */
    public void update(int threadId, int pos) {
        this.data.put(threadId, pos);
    }

    /**
     * 保存记录文件
     */
    public synchronized void saveLogFile() {
        BasicDataInfo.updateDownLenght(mSQLiteDatabase, this.downloadUrl, this.data);
        // this.fileService.update(this.downloadUrl, this.data);
        // this.fileService.update(this.downloadUrl, this.data);
    }

    public void stopThreads() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (threadList.size() > 0) {
                    if (threadList.get(0).isAlive()) {
                        threadList.get(0).forceEnd();
                    } else {
                        threadList.remove(0);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 构建文件下载器
     *
     * @param downloadUrl 下载路径
     * @param fileSaveDir 文件保存目录
     * @param threadNum   下载线程数
     */
    public SmartFileDownloader(Context context, String downloadUrl, File fileSaveDir, int threadNum, String packageName) {
        state = 8;
//		appId = id;
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        mFileSaveDir = fileSaveDir.getPath();
        LogDebugUtil.d("PackageUtils", "----111-----.." + fileSaveDir.getPath());
        loader = this;
        mThreadNum = threadNum;
        this.mSQLiteDatabase = VpnStoreApplication.app.getSQLDatabase();
        this.context = context;
        record = BasicDataInfo.getRecord(mSQLiteDatabase, downloadUrl);
    }

    public void getApkMsg(File fileSaveDir, Handler handler) {
        try {
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            this.threads = new SmartDownloadThread[mThreadNum];
            if ((record != null && !record.getFileName().equals("")) && record.getState() != 5 && record.getState() != 7 && record.getState() != 8 && record.getState() != 9) {
                this.fileSize = record.getFileSize();
                // if (this.fileSize <= 0)
                // throw new RuntimeException("Unkown file size ");
                String filename = record.getFileName();
                this.fileName = filename;
                LogDebugUtil.d("PackageUtils", "---------.." + fileSaveDir.getPath());
                this.saveFile = new File(fileSaveDir, filename);/* 保存文件 */
                String command = "chmod 777 " + this.saveFile.getAbsolutePath();
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(command);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Map<Integer, Integer> logdata = BasicDataInfo.getDownData(mSQLiteDatabase, downloadUrl);
                // Map<Integer, Integer> logdata =
                // fileService.getData(downloadUrl);
                if (logdata.size() > 0) {
                    for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
                        data.put(entry.getKey(), entry.getValue());
                }
                this.downloadSize = 0;
                this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
                if (this.data.size() == this.threads.length) {
                    for (int i = 0; i < this.threads.length; i++) {
                        this.downloadSize += this.data.get(i + 1);
                    }
                }
                oldDownloadSize = downloadSize;
                state = record.getState();
//                if (this.downloadSize >= 0 && this.downloadSize < this.fileSize) {
//                    state = 2;
//                }
                // if (this.downloadSize == this.fileSize) {
                // state = 3;
                // }
                // if (record.getState() > 3) {
                // state = record.getState();
                // }
                if (state == 6) {
                    state = 2;
                }
                if (state == 8) {
                    state = 9;
                }
                BasicDataInfo.updateDownRecord(mSQLiteDatabase, this.downloadSize, this.fileSize, this.downloadUrl, this.fileName);
                BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
                Message msg = new Message();
                msg.what = 1;
                StateInfo info = new StateInfo();
                info.setDownloadSize(downloadSize);
                info.setFileSize(fileSize);
                info.setState(state);
//				info.setId(record.getAppId());
                info.setPackageName(packageName);
                msg.obj = info;
                handler.sendMessage(msg);

            } else {
                getApkInfo(downloadUrl, fileSaveDir, handler);
            }
        } catch (Exception e) {
            state = 7;
            downloadSize = 0;
            BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
            BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
            BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);
            Message msg = new Message();
            msg.what = 2;
            StateInfo info = new StateInfo();
            info.setDownloadSize(downloadSize);
            info.setFileSize(fileSize);
            info.setState(state);
//			info.setId(appId);
            info.setPackageName(packageName);
            msg.obj = info;
            handler.sendMessage(msg);
            Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_DB);
            context.sendBroadcast(intent1);
            Log.e("kzy1", "kzy1");
            print(e.toString());
        }
    }

    private void getApkInfo(String path, File fileSaveDir, Handler handler) {
        // state = 1;
        // String requestUrl = HttpUtils.proxyUrl(path);//暂时没用加速SDK
//		Address address = HttpUtils.getAddress();
//		if (address != null) 
        {
            try {
                URL url = new URL(path);
//				String host = address.getHost();
//				int port = address.getPort();
//				java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(host, port));
//				infoConn = (HttpURLConnection) url.openConnection(proxy);
                infoConn = (HttpURLConnection) url.openConnection();
                infoConn.setConnectTimeout(30000);
                infoConn.setReadTimeout(30000);
                // conn.setConnectTimeout(1000);
                // System.setProperty("sun.net.client.defaultConnectTimeout",
                // "1000");
                infoConn.setRequestMethod("GET");
                infoConn.setInstanceFollowRedirects(false);
                infoConn.setRequestProperty("Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                infoConn.setRequestProperty("Accept-Language", "zh-CN");
                infoConn.setRequestProperty("Referer", downloadUrl.toString());
                infoConn.setRequestProperty("Charset", "UTF-8");
                infoConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                // conn.setRequestProperty("Connection", "Keep-Alive");

                int responseCode = infoConn.getResponseCode();

                if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_PARTIAL == responseCode) {
                    this.fileSize = infoConn.getContentLength();// 根据响应获取文件大小
                    if (this.fileSize <= 0) {
//						synchronized (lock) 
                        {
                            if (!isForce) {
                                state = 7;
                                downloadSize = 0;
                            }
                        }
                        BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
                        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                        BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);
                        Message msg = new Message();
                        msg.what = 2;
                        StateInfo info = new StateInfo();
                        info.setDownloadSize(downloadSize);
                        info.setFileSize(fileSize);
                        info.setState(state);
//						info.setId(appId);
                        info.setPackageName(packageName);
                        msg.obj = info;
                        handler.sendMessage(msg);
                        Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                        context.sendBroadcast(intent1);
                        // throw new RuntimeException("Unkown file size ");
                    } else {
                        String filename = getFileName(infoConn);
                        this.fileName = filename;
                        this.saveFile = new File(fileSaveDir, filename);/* 保存文件 */
                        String command = "chmod 777 " + this.saveFile.getAbsolutePath();
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec(command);
                        record = BasicDataInfo.getRecord(mSQLiteDatabase, downloadUrl);
                        Map<Integer, Integer> logdata = BasicDataInfo.getDownData(mSQLiteDatabase, downloadUrl);
                        // Map<Integer, Integer> logdata =
                        // fileService.getData(downloadUrl);
                        if (logdata.size() > 0) {
                            for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
                                data.put(entry.getKey(), entry.getValue());
                        }
                        this.downloadSize = 0;
                        this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
                        if (this.data.size() == this.threads.length) {
                            for (int i = 0; i < this.threads.length; i++) {
                                this.downloadSize += this.data.get(i + 1);
                            }
                            print("已经下载的长度" + this.downloadSize);
                        }
                        oldDownloadSize = downloadSize;
                        if (record == null) {
                            record = new DownRecordInfo();
                        }
                        record.setDownlength(this.downloadSize);
                        record.setFileSize(this.fileSize);
                        record.setFileName(this.fileName);
//						synchronized (lock) 
                        {
                            if (!isForce) {
                                state = 1;
                                if (this.downloadSize == this.fileSize && downloadSize > 0) {
                                    state = 3;
                                }
//								if (Pref.getInt(Pref.DOWNLOAD_NUMBER, context) >= 2) {
//									state = 6;
//								}
                            }
                        }
                        BasicDataInfo.updateDownRecord(mSQLiteDatabase, this.downloadSize, this.fileSize, this.downloadUrl, this.fileName);
                        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
                        Message msg = new Message();
                        msg.what = 1;
                        StateInfo info = new StateInfo();
                        info.setDownloadSize(downloadSize);
                        info.setFileSize(fileSize);
                        info.setState(state);
//						info.setId(record.getAppId());
                        info.setPackageName(packageName);
                        msg.obj = info;
                        handler.sendMessage(msg);
                    }
                } else if (responseCode == 301 || responseCode == 302) {
                    Log.e("server response 301302 ", "server no response 301302");
                    String location = infoConn.getHeaderField("Location");
                    getApkInfo(location, fileSaveDir, handler);
                } else {
//					synchronized (lock) 
                    {
                        if (!isForce) {
                            state = 7;
                            downloadSize = 0;
                        }
                    }
                    BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
                    BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                    BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);
                    Message msg = new Message();
                    msg.what = 2;
                    StateInfo info = new StateInfo();
                    info.setDownloadSize(downloadSize);
                    info.setFileSize(fileSize);
                    info.setState(state);
//					info.setId(appId);
                    info.setPackageName(packageName);
                    msg.obj = info;
                    handler.sendMessage(msg);
                    Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                    context.sendBroadcast(intent1);
                    Log.e("server no response ", "server no response ");
                    throw new RuntimeException("server no response ");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (!isForce) {
//					synchronized (lock) 
                    {
                        state = 7;
                        downloadSize = 0;
                    }
                    BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
                    BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                    BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);
                    Message msg = new Message();
                    msg.what = 2;
                    StateInfo info = new StateInfo();
                    info.setDownloadSize(downloadSize);
                    info.setFileSize(fileSize);
                    info.setState(state);
//					info.setId(appId);
                    info.setPackageName(packageName);
                    msg.obj = info;
                    handler.sendMessage(msg);
                    Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                    context.sendBroadcast(intent1);
                } else {
                }
                Log.e("e.toString()", "e.toString()");
                print(e.toString());
            }

        }

    }

    public void tryAgain(String path, File fileSaveDir, Handler handler) {
        record = BasicDataInfo.getRecord(mSQLiteDatabase, downloadUrl);
        this.threads = new SmartDownloadThread[mThreadNum];
        getApkInfo(path, fileSaveDir, handler);
    }

    // 完整的安装文件被人为的删除时点击安装时从新下载文件
    public void tryAgain2(String path, File fileSaveDir, Handler handler) {
        state = 8;
        record = BasicDataInfo.getRecord(mSQLiteDatabase, downloadUrl);
        if (threads != null && threads.length > 0) {
            for (int i = 0; i < threads.length; i++) {
                if (threads[i] != null)
                    threads[i].setStop(true);
            }
        }
        BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
        data.clear();// 清除数据
        for (int i = 0; i < threads.length; i++) {
            data.put(i + 1, 0);
        }
        downloadSize = 0;
        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
        BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
        this.threads = new SmartDownloadThread[mThreadNum];
        getApkInfo(path, fileSaveDir, handler);
    }

    /**
     * 获取文件名
     */
    private String getFileName(HttpURLConnection conn) {
        String filename = conn.getURL().getFile();
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        if (!filename.endsWith("apk")) {
            filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1) + ".apk";// 默认取一个文件名
        }
        return filename;
    }

    /**
     * 开始下载文件
     *
     * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     * @throws Exception
     */
    public int download(final SmartDownloadProgressListener listener) throws Exception {
        try {
            //对下载大小进行校正
            this.downloadSize = 0;
            if (this.data.size() == this.threads.length) {
                for (int i = 0; i < this.threads.length; i++) {
                    this.downloadSize += this.data.get(i + 1);
                }
            }
            oldDownloadSize = downloadSize;
            current = System.currentTimeMillis();
            stop = false;
            isLoad = true;
            state = 1;
            BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
            if (downloadSize > 0 && !saveFile.exists()) {
                if (threads != null && threads.length > 0) {
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i] != null)
                            threads[i].setStop(true);
                    }
                }
                BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
                data.clear();// 清除数据
                for (int i = 0; i < threads.length; i++) {
                    data.put(i + 1, 0);
                }
                state = 5;
                downloadSize = 0;
                BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
                isLoad = false;
                if (listener != null) {
                    listener.onDownloadSize(downloadSize, fileSize, state, "0KB/S", packageName);
                    BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
                }
                return this.downloadSize;
            }
            String command = "chmod 777 " + this.saveFile.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
            RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");

            if (this.fileSize > 0)
                randOut.setLength(this.fileSize);
            randOut.close();
            final String url = this.downloadUrl;
            if (this.data.size() != this.threads.length) {
                this.data.clear();// 清除数据
                for (int i = 0; i < this.threads.length; i++) {
                    this.data.put(i + 1, 0);
                }
            }
            BasicDataInfo.saveDownLenght(mSQLiteDatabase, this.downloadUrl, this.data);
            this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
            for (int i = 0; i < this.threads.length; i++) {
                int downLength = this.data.get(i + 1);
                if (downLength < this.block && this.downloadSize < this.fileSize) { // 该线程未完成下载时,继续下载
                    this.threads[i] = new SmartDownloadThread(this, url, this.saveFile, this.block, this.data.get(i + 1), i + 1);
                    // this.threads[i].setPriority(7);
                    this.threads[i].setPackName(packageName);
                    threads[i].setOSPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    this.threads[i].start();
                    threadList.add(this.threads[i]);
                } else {
                    this.threads[i] = null;
                }
            }
            // this.fileService.save(this.downloadUrl, this.data);
            Thread newThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    boolean notFinish = true;// 下载未完成
                    int times = 0;
                    while (notFinish) {// 循环判断是否下载完毕
                        if (stop) {
                            stopThreads();
                            break;
                        }
                        if (downloadSize > fileSize) {//下载出错（下载长度大于文件长度）
                            if (saveFile.exists()) {
                                saveFile.delete();
                            }
                        }
                        if (!saveFile.exists()) {
                            if (threads != null && threads.length > 0) {
                                for (int i = 0; i < threads.length; i++) {
                                    if (threads[i] != null)
                                        threads[i].setStop(true);
                                }
                            }
                            BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
                            data.clear();// 清除数据
                            for (int i = 0; i < threads.length; i++) {
                                data.put(i + 1, 0);
                            }
                            break;
                        }
                        notFinish = false;// 假定下载完成
                        for (int i = 0; i < threads.length; i++) {
                            if (threads[i] != null && !threads[i].isFinish()) {
                                notFinish = true;//下载没有完成
                                if (threads[i].getDownLength() == -1) {//如果下载失败,再重新下载
                                    threads[i] = new SmartDownloadThread(loader, url, saveFile, block, data.get(i + 1), i + 1);
                                    threads[i].setPackName(packageName);
                                    threads[i].setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                    threads[i].start();
                                }
                            }
                        }

                        float size = (float) downloadSize * 100 / (float) fileSize;
                        DecimalFormat format = new DecimalFormat("0.0");
                        String progress = format.format(size);
                        float more = (float) (downloadSize - oldDownloadSize);
                        float speed = more / (float) (System.currentTimeMillis() - current);
                        current = System.currentTimeMillis();
                        String speedStr = format.format(speed) + "KB/S";
                        if (listener != null) {
                            BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
                            listener.onDownloadSize(downloadSize, fileSize, state, speedStr, packageName);
                        }
                        oldDownloadSize = downloadSize;
                        if (downloadSize == fileSize) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }//while (notFinish) {// 循环判断是否下载完毕

                    PackageManager pm = context.getPackageManager();
                    String archiveFilePath = mFileSaveDir + "/" + fileName;
                    if (!StorageUtils.isExternalStorageWritable()) {
                        StorageUtils.getFilesAuthority(fileName);
                    }
                    final PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
//					if (stop && downloadSize < fileSize) {
                    if (stop) {
                        state = 2;
                        isLoad = false;
                        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                    } else {

//						if (downloadSize == fileSize && fileSize > 0) {
                        state = 3;
                        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
                        Intent intent2 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                        context.sendBroadcast(intent2);
                        isLoad = false;
                        if (info != null) {
                            //下载完成上报
                            ModeLevelAmsUpload.uploadOperateData(context, info.applicationInfo.packageName, info.versionName, "DOWNLOAD_SUCCESS", false);
                            //只有在商城界面才会弹出应用安装。。
//								if(AppManager.isTopActivity(context)) {
                            String str = "/" + fileName;

                            String fn = mFileSaveDir + str;

                            PackageUtils.install(context, fn, record.getAppName(), info.applicationInfo.packageName, new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    // TODO Auto-generated method stub
                                    super.handleMessage(msg);
                                    if (msg.what != PackageUtils.INSTALL_SUCCEEDED) {
                                        if (info.applicationInfo.packageName != null) {
                                            LoggerUtil.w(context.getString(R.string.app_install_error) + ":"
                                                            + info.applicationInfo.packageName,
                                                    "freeMemory:" + StorageUtils.getAvailableInternalMemorySize(context) + "\n"
                                                            + PackageUtils.getFailReason(msg.what));
                                        } else {
                                            LoggerUtil.w(context.getString(R.string.app_install_error),
                                                    "freeMemory:" + StorageUtils.getAvailableInternalMemorySize(context) + "\n"
                                                            + PackageUtils.getFailReason(msg.what));
                                        }
                                        if (msg.what == PackageUtils.INSTALL_FAILED_INSUFFICIENT_STORAGE
                                                || msg.what == PackageUtils.INSTALL_FAILED_DEXOPT) {
                                            ToastUtils.show(context, record.getAppName() + "安装失败:储存不足");
                                        }
                                        if (msg.what == PackageUtils.INSTALL_FAILED_INVALID_URI) {
//                                            LoggerUtil.w(context.getString(R.string.app_install_error), "手动下载" + record.getAppName());
                                            ToastUtils.show(context, record.getAppName() + "安装失败");
                                        }
                                    }
                                }
                            });
//								}
                        } else {
                            PackageUtils.errorInstallBroadcast(context, packageName);
                            Message message = Message.obtain();
                            message.obj = archiveFilePath;
                            message.what = 100;
                            handler.sendMessage(message);
                        }
                    }
                    if (info != null) {
                        ApplicationInfo appInfo = info.applicationInfo;
                        String packageName = appInfo.packageName; // 得到安装包名称
                        record.setPackageName(packageName);
                        record.setAppVersion(info.versionName);
                        BasicDataInfo.updateDownRecordpackageName(mSQLiteDatabase, downloadUrl, packageName);
                        BasicDataInfo.updateDownRecordAppVersion(mSQLiteDatabase, downloadUrl, info.versionName);
                    }
                    BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);

                    Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
                    context.sendBroadcast(intent1);
                    if (listener != null) {
                        listener.onDownloadSize(downloadSize, fileSize, state, "0KB/S", packageName);
                    }
                }

            });
            newThread.start();
        } catch (Exception e) {
            state = 5;
            BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
            BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
            isLoad = false;
            if (listener != null) {
                listener.onDownloadSize(downloadSize, fileSize, state, "0KB/S", packageName);
                BasicDataInfo.updateDownRecord(mSQLiteDatabase, downloadSize, fileSize, downloadUrl, fileName);
            }
            print(e.toString());
            throw new Exception("file download fail");
        }
        return this.downloadSize;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                String path = (String) msg.obj;
                LoggerUtil.w(context.getString(R.string.app_install_error), "手动下载包异常" + packageName + ":" + path);
                ToastUtils.show(context, "抱歉！文件解析错误，请尝试重新下载安装该应用！");
            }
        }
    };

    public void forceStop() {
//		synchronized (lock) 
        {
            isForce = true;
            state = 9;
            downloadSize = 0;
            fileName = "";
            fileSize = 0;
            if (infoConn != null) {
                infoConn.disconnect();
            }
        }
        BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
        BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);
        Intent intent = new Intent(DisplayConfig.ACTION_RESULT_MESSAGE);
//		intent.putExtra("appId", appId);
        intent.putExtra("fileSize", fileSize);
        intent.putExtra("downloadSize", downloadSize);
        intent.putExtra("state", state);
        intent.putExtra("speed", "已暂停");
        intent.putExtra("packageName", packageName);
        context.sendBroadcast(intent);
        Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
        context.sendBroadcast(intent1);
//		appId = -1;
        downloadUrl = "";
        stop();

    }

    public void clear() {
        stop = true;
        if (this.threads == null) {
            return;
        }
        if (this.threads != null && this.threads.length > 0) {
            for (int i = 0; i < threads.length; i++) {
                if (this.threads[i] != null)
                    this.threads[i].setStop(true);
                this.threads[i] = null;
            }
        }
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
        BasicDataInfo.deleteDownRecord(mSQLiteDatabase, downloadUrl);
//		BasicDataInfo.updateDownRecordState(mSQLiteDatabase, downloadUrl, state);
//		BasicDataInfo.updateDownRecord(mSQLiteDatabase, 0, 0, downloadUrl, fileName);

    }

    public void destroyThread() {
        if (this.threads == null) {
            return;
        }
        for (int i = 0; i < this.threads.length; i++) {
            if (threads[i] == null) {
                continue;
            }
            if (threads[i].isAlive()) {
                threads[i].setStop(true);
            }
        }
    }

    public void stop() {
        stop = true;
        state = 2;
        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
    }

    public void pause() {
        if (state == 7) {
            return;
        }
        if (this.threads == null) {
            return;
        }
        if (this.threads != null && this.threads.length > 0) {
            for (int i = 0; i < threads.length; i++) {
                if (this.threads[i] != null)
                    synchronized (this.threads[i]) {
                        this.threads[i].Pause();
                    }
            }
        }
    }

    public void reStart() {
        if (this.threads != null && this.threads.length > 0) {
            for (int i = 0; i < threads.length; i++) {
                this.threads[i].Resume();
            }
            state = 1;
            BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
        }
    }

    /**
     * 获取Http响应头字段
     *
     * @param http
     * @return
     */
    public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0; ; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null)
                break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }

    /**
     * 打印Http头字段
     *
     * @param http
     */
    public static void printResponseHeader(HttpURLConnection http) {
        Map<String, String> header = getHttpResponseHeader(http);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey() + ":" : "";
            print(key + entry.getValue());
        }
    }

    // 打印日志
    private static void print(String msg) {
        Log.i(TAG, msg);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(int downloadSize) {
        this.downloadSize = downloadSize;
    }

    public SmartDownloadThread[] getThreads() {
        return threads;
    }

    public void setThreads(SmartDownloadThread[] threads) {
        this.threads = threads;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    public Map<Integer, Integer> getData() {
        return data;
    }

    public void setData(Map<Integer, Integer> data) {
        this.data = data;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public static String getTag() {
        return TAG;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        BasicDataInfo.updateDownRecordState(mSQLiteDatabase, this.downloadUrl, state);
        Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_RECORD);
        context.sendBroadcast(intent1);

    }

    public String getFileName() {
        return fileName;
    }

    // public DownRecordInfo getRecord() {
    // return record;
    // }

    public void setRecord(DownRecordInfo record) {
        this.record = record;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean isLoad) {
        this.isLoad = isLoad;
    }

    public boolean isDownloadIn2g3g() {
        return isDownloadIn2g3g;
    }

    public void setDownloadIn2g3g(boolean isDownloadIn2g3g) {
        this.isDownloadIn2g3g = isDownloadIn2g3g;
    }

//	public int getId() {
//		return appId;
//	}

    public void setForce(boolean isForce) {
        this.isForce = isForce;
    }

    public String getPackageName() {
        return packageName;
    }

}
