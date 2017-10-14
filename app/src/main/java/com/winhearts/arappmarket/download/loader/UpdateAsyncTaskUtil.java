package com.winhearts.arappmarket.download.loader;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.UIHelper;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * 应用异常下载
 */
public class UpdateAsyncTaskUtil extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "UpdateAsyncTaskUtil";
    private ProgressBar progressBar = null;
    private TextView tvPercent = null;
    private Context mContext;
    private Dialog mDialog;
    private String apkName;
    private String mFilePath;
    private Handler myHandler;
    boolean cancelable;

    public void setHandler(Handler handler) {
        this.myHandler = handler;
    }

    public UpdateAsyncTaskUtil(Context mContext, boolean cancelable) {
        this.mContext = mContext;
        this.cancelable = cancelable;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_update_processing, null);
        tvPercent = (TextView) view.findViewById(R.id.tvPercent);
        progressBar = (ProgressBar) view.findViewById(R.id.pbProcessing);

        mDialog = new Dialog(mContext, R.style.dialog);

        mDialog.setCancelable(false);
        mDialog.setContentView(view);
        if (cancelable) {
            View btn1 = view.findViewById(R.id.btn_update_dialog_background);
            btn1.setVisibility(View.VISIBLE);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    if (myHandler != null) {
                        LogDebugUtil.i(TAG, "DOWNLOAD_BACKGROUND");
                        myHandler.sendEmptyMessage(Constant.DOWNLOAD_BACKGROUND);
                    }
                }
            });
            View btn2 = view.findViewById(R.id.btn_update_dialog_cancel);
            btn2.setVisibility(View.VISIBLE);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateAsyncTaskUtil.this.cancel(true);
                    mDialog.dismiss();
                    if (myHandler != null) {
                        LogDebugUtil.i(TAG, "CANCEL_DOWNLOAD");
                        myHandler.sendEmptyMessage(Constant.CANCEL_DOWNLOAD);
                    }
                }
            });
        }

        // 设置window属性
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            // 重设宽度
            lp.width = ScreenUtil.dip2px(mContext, 614);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mDialog.show();
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        BufferedInputStream bis = null;
        RandomAccessFile randomAccessFile = null;
        URLConnection conn;
        try {

            String url = params[0];
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + params[0];
            }
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());

            URL downLoadUrl = new URL(url);
            conn = downLoadUrl.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setConnectTimeout(12000);
            conn.setReadTimeout(12000);
            conn.setRequestProperty("Connection", "Keep-Alive");
            apkName = params[1];
            File localFile;
            if (StorageUtils.isExternalStorageWritable()) {
                localFile = new File(Environment.getExternalStorageDirectory(), params[1]);
                mFilePath = localFile.getPath();
            } else {
                localFile = StorageUtils.getFilesAuthority(apkName);
            }
            //删除原有下载的文件
            if (localFile.exists()) {
                FileUtils.forceDelete(localFile);
            }

            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(conn.getInputStream());
            randomAccessFile = new RandomAccessFile(localFile, "rwd");
            int fileSize = conn.getContentLength();
            int downloadLength = 0;
            int len;
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                if (isCancelled()) {
                    return false;
                }
                randomAccessFile.write(buffer, 0, len);
                downloadLength += len;
                publishProgress((int) ((downloadLength / (float) fileSize) * 100));
            }
            return true;


        } catch (Exception e) {
            LoggerUtil.e(mContext.getResources().getString(R.string.appmarket_download_error), e.getMessage());
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (bis != null) {
                    bis.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            PrefNormalUtils.putString(mContext, PrefNormalUtils.APP_UPDATE, Util.getVersionCode(mContext));
            // 安装
            if (StorageUtils.isExternalStorageWritable()) {
                UIHelper.installApk(mContext, apkName);
            } else {
                UIHelper.installApkTmp(mContext, apkName);
            }
            if (myHandler != null) {
                myHandler.sendEmptyMessage(Constant.DOWNLOAD_SUCCESS);
            }
        } else {
            if (myHandler != null) {
                LogDebugUtil.i(TAG, "DOWNLAOD_FAIL");
                myHandler.sendEmptyMessage(Constant.DOWNLOAD_FAIL);
            }
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {

    }

    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int vlaue = values[0];
        progressBar.setProgress(vlaue);
        tvPercent.setText(String.format("%s%%", String.format("%d", vlaue)));
    }

    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    private class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
