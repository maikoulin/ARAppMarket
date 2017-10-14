package com.winhearts.arappmarket.download.loader;

import java.net.HttpURLConnection;
import java.net.URL;
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
 * Description:下载工具
 * Created by lmh on 2016/4/12.
 */
public class DownloadUtil {
    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    private class CustTrustManager implements X509TrustManager {
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

    public Integer getFileLength(String url) {
        HttpURLConnection conn = null;
        int fileSize = 0;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new CustTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            URL downLoadUrl = new URL(url);
            conn = (HttpURLConnection) downLoadUrl.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            System.setProperty("http.keepAlive", "false");
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            fileSize = conn.getContentLength();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return fileSize;
    }


}
