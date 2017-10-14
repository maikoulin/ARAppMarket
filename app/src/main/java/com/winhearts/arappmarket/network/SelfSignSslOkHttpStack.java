package com.winhearts.arappmarket.network;

import android.util.Log;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A HttpStack implement witch can verify specified self-signed certification.
 * 有对https 做信任的请求
 */
public class SelfSignSslOkHttpStack extends HurlStack {

    private OkHttpClient okHttpClient;

    private Map<String, SSLSocketFactory> socketFactoryMap = null;
    private SSLSocketFactory sslSocketFactory;
    private MyHostnameVerifier myHostnameVerifier;

    /**
     * Create a OkHttpStack with default OkHttpClient.
     */
    public SelfSignSslOkHttpStack() {
        this.okHttpClient = new OkHttpClient();
        //重定向
        this.okHttpClient.setFollowSslRedirects(false);
        this.okHttpClient.setFollowRedirects(false);
        try {
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        myHostnameVerifier = new MyHostnameVerifier();
    }

    /**
     * Create a OkHttpStack with default OkHttpClient.
     */
    public SelfSignSslOkHttpStack(Map<String, SSLSocketFactory> factoryMap) {
        this(new OkHttpClient(), factoryMap);
    }

    /**
     * Create a OkHttpStack with a custom OkHttpClient
     *
     * @param okHttpClient Custom OkHttpClient, NonNull
     */
    public SelfSignSslOkHttpStack(OkHttpClient okHttpClient, Map<String, SSLSocketFactory> factoryMap) {
        this.okHttpClient = okHttpClient;
        this.socketFactoryMap = factoryMap;
        this.okHttpClient.setFollowSslRedirects(false);
        this.okHttpClient.setFollowRedirects(false);
        try {
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        myHostnameVerifier = new MyHostnameVerifier();
    }


    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {

        //客户端有导入签名的用这种
//        if ("https".equals(url.getProtocol()) && socketFactoryMap != null && socketFactoryMap.containsKey(url.getHost())) {
//            HttpsURLConnection connection = (HttpsURLConnection) new OkUrlFactory(okHttpClient).open(url);
//            connection.setSSLSocketFactory(socketFactoryMap.get(url.getHost()));
//            return connection;
//        }

        //无条件支持签名的用这种
        if ("https".equals(url.getProtocol())) {
            HttpsURLConnection connection = (HttpsURLConnection) new OkUrlFactory(okHttpClient).open(url);
            try {
                connection.setSSLSocketFactory(sslSocketFactory);
                connection.setHostnameVerifier(myHostnameVerifier);
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage());
            }
            return connection;
        } else {
            return new OkUrlFactory(okHttpClient).open(url);
        }
    }

    /*************
     * 做所有都认证
     ***********/
    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    private class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
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
