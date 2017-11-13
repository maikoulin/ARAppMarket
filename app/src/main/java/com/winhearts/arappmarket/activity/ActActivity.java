package com.winhearts.arappmarket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.ActivityInfo;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;

/**
 * 活动页
 * Created by lmh on 2016/1/27.
 */
public class ActActivity extends BaseActivity {
    private final String TAG = "ActActivity";
    public final static int REQUEST_LOGIN = 0;

    private WebView webView;
    private ActivityInfo activityInfo;
    private View loadView;
    private boolean isCanBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
        webView = (WebView) findViewById(R.id.webView_act);
        activityInfo = (ActivityInfo) getIntent().getSerializableExtra("actInfo");
        loadView = findViewById(R.id.progress_act);
        initWebView();
        if (TextUtils.isEmpty(ConstInfo.accountWinId)) {
            startActivityForResult(new Intent(this, AccountLoginActivity.class), REQUEST_LOGIN);
        } else {
            webView.loadUrl(initUrl());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (AccountManagerActivity.ACK_LOGIN_COMPLETED == resultCode) {
                webView.loadUrl(initUrl());
            } else {
                finish();
            }
        }
    }

    private String initUrl() {
        StringBuilder stringBuilder = new StringBuilder(activityInfo.getUrl());
        stringBuilder.append("?");

        stringBuilder.append("loginToken=");
        stringBuilder.append(ConstInfo.accountTokenId);
        stringBuilder.append("&");

        stringBuilder.append("winId=");
        stringBuilder.append(ConstInfo.accountWinId);
        stringBuilder.append("&");

        stringBuilder.append("mac=");
        stringBuilder.append(MacUtil.getMacAddress());

        LogDebugUtil.i(TAG, "webUrl:" + stringBuilder.toString());
        return stringBuilder.toString();
    }


    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);// 支持jsp
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
//        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webView.getSettings().setBlockNetworkImage(true);//把图片加载放在最后来加载渲染
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed(); //接受证书
                //handleMessage(Message msg); 其他处理
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadView.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setFocusable(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕,单列居中
        webView.setBackgroundColor(0);
//        webView.getBackground().setAlpha(0);
        webView.addJavascriptInterface(new JsToJava(), "androidWeb");
    }

    class JsToJava {

        @JavascriptInterface
        public void isReturnGoBack(boolean isCanBack) {
            ActActivity.this.isCanBack = isCanBack;
            LogDebugUtil.i(TAG, "isCanBack:" + isCanBack);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack();// 返回前一个页面
                return true;
            }
            if (!isCanBack) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
