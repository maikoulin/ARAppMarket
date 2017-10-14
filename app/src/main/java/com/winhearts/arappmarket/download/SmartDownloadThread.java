package com.winhearts.arappmarket.download;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.StorageUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载文件的线程
 */
public class SmartDownloadThread extends Thread {
    private Object mPauseLock;
    private boolean mPauseFlag;
    private static final String TAG = "SmartDownloadThread";
    private File saveFile;
    private String downUrl;
    private int block;
    private boolean stop = false;
    /* *下载开始位置 */
    private int threadId = -1;
    private int downLength;
    private int oldLength;
    private boolean finish = false;
    private SmartFileDownloader downloader;
    private int mOSPriority = Process.THREAD_PRIORITY_BACKGROUND;

    private RandomAccessFile threadfile;
    private InputStream inStream;
    private HttpURLConnection httpURLConnection;

    private String packageName;

    public SmartDownloadThread(SmartFileDownloader downloader, String downUrl, File saveFile, int block, int downLength, int threadId) {
        this.downUrl = downUrl;
        this.saveFile = saveFile;
        this.block = block;
        this.downloader = downloader;
        this.threadId = threadId;
        this.downLength = downLength;
        this.oldLength = downLength;
        mPauseLock = new Object();
        mPauseFlag = false;
    }

    //原则上不建议这么做滴。。
    public void setPackName(String packageName) {
        if (packageName != null) {
            this.packageName = packageName;
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(mOSPriority);
        downApk(this.downUrl);
    }

    private void downApk(String downPath) {
        if (downLength < block) {// 未下载完成
            try {
                boolean ret = true;
                httpURLConnection = null;
                Log.e("url", "" + downPath);
                URL url = new URL(downPath);
//				Address address = HttpUtils.getAddress();
//				if (address != null) 
                {
//					String host = address.getHost();
//					int port = address.getPort();
//					java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(host, port));
//					httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setInstanceFollowRedirects(false);
//					httpURLConnection.setRequestProperty("Accept",
//							"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//					httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
                    httpURLConnection.setRequestProperty("Referer", downPath.toString());
//					httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    int startPos = block * (threadId - 1) + downLength;// 开始位置
                    int endPos = block * threadId - 1;// 结束位置
                    httpURLConnection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);// 设置获取实体数据的范围
//					httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

                    int responseCode = httpURLConnection.getResponseCode();
                    Log.e("url", "responseCode : " + responseCode);
                    if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_PARTIAL == responseCode) {// 连接成功
                        inStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024 * 20];
                        int offset = 0;
                        print("Thread " + this.threadId + " start download from position " + startPos);
                        threadfile = new RandomAccessFile(this.saveFile, "rwd");
                        threadfile.seek(startPos);
                        while (ret) {
//							pauseThread();
//							print("Thread " + this.threadId + "stop2222222222: " + stop);
                            if ((offset = inStream.read(buffer, 0, 1024 * 20)) == -1 || downLength >= block) {
                                ret = false;
                            } else {
                                if (downloader != null) {
//									print("Thread " + this.threadId + "stop: " + stop);
//									print("Thread " + this.threadId + "downLength " + downLength);
                                    threadfile.write(buffer, 0, offset);
                                    downLength += offset;
                                    downloader.append(offset);
                                    downloader.update(this.threadId, downLength);
                                    downloader.saveLogFile();
                                }
                            }
//							Thread.sleep(10);
                            if (stop) {
                                downloader = null;
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                httpURLConnection.disconnect();
//								this.interrupt();
                                break;
                            }
                        }
                        threadfile.close();
                        inStream.close();
                        if (!stop) {
                            print("Thread " + this.threadId + " download finish");
                            this.finish = true;
                        } else {
                            httpURLConnection.disconnect();
                        }
                    } else if (responseCode == 301 || responseCode == 302) {
                        Log.e("url", "responseCode : " + responseCode);
                        String location = httpURLConnection.getHeaderField("Location");
                        downApk(location);
                    }
                }
            } catch (Exception e) {
                if (!TextUtils.isEmpty(packageName)) {
                    if (ManagerUtil.checkApplication(VpnStoreApplication.getApp(), packageName)) {
                        LoggerUtil.w("用户应用更新-下载失败：" + packageName,
                                "freeMemory:" + StorageUtils.getAvailableInternalMemorySize(VpnStoreApplication.getApp()) + "\n"
                                        + "downLength:" + downLength + "\n" + e);
                    } else {
                        LoggerUtil.w("用户应用下载失败：" + packageName,
                                "freeMemory:" + StorageUtils.getAvailableInternalMemorySize(VpnStoreApplication.getApp()) + "\n"
                                        + "downLength:" + downLength + "\n" + e);
                    }
                } else {
                    LoggerUtil.w("用户应用下载失败：" + downPath,
                            "freeMemory:" + StorageUtils.getAvailableInternalMemorySize(VpnStoreApplication.getApp()) + "\n"
                                    + "downLength:" + downLength + "\n" + e);
                }
                e.printStackTrace();
                this.downLength = -1;
                print("Thread " + this.threadId + ":" + e);
                //SmartDownloadThread: Thread 1:java.io.IOException: write failed: ENOSPC (No space left on device)
            }
        }
    }

    public void forceEnd() {
        stop = true;
    }

    private static void print(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * 下载是否完成
     *
     * @return
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * 已经下载的内容大小
     *
     * @return 如果返回值为-1,代表下载失败
     */
    public int getDownLength() {
        return downLength;
    }

    public void Pause() {
        synchronized (mPauseLock) {
            mPauseFlag = true;
        }
    }

    public void Resume() {
        synchronized (mPauseLock) {
            mPauseFlag = false;
            mPauseLock.notify();
        }
    }

    private void pauseThread() {
        synchronized (mPauseLock) {
            if (mPauseFlag) {
                try {
                    mPauseLock.wait();
                } catch (Exception e) {
                    Log.v("thread", "fails");
                }
            }
        }
    }

    public boolean ismPauseFlag() {
        return mPauseFlag;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setOSPriority(int p) {
        mOSPriority = p;
    }

    public int getOldLength() {
        return oldLength;
    }

    public void setOldLength(int oldLength) {
        this.oldLength = oldLength;
    }

}
