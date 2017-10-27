package com.winhearts.arappmarket.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.model.ReturnMessage;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.service.DownloadService;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.LoadAndErrorView;


public class BaseActivity extends BaseMostActivity {
    protected String SIMPLE_NAME = this.getClass().getSimpleName();
    protected boolean isCleanBackRecord = false;
    private boolean isRestore = false;
    private LoadAndErrorView mTipsView;

    //需要申请GETTask权限
    private boolean isApplicationBroughtToBackground() {
        String packageName = ManagerUtil.getTopActivityName(this);
        return !TextUtils.isEmpty(packageName) && !packageName.equals(getPackageName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isApplicationBroughtToBackground()) {
            putIsBackground(true);
        }
    }

    private void putIsBackground(boolean isBackground) {
        PrefNormalUtils.putBoolean(PrefNormalUtils.IS_BACKGROUND, isBackground);
    }

    private boolean getIsBackground() {
        return PrefNormalUtils.getBoolean(PrefNormalUtils.IS_BACKGROUND, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRestore) {   //如果是进程在后台被kill时需多判断
            if (ManagerUtil.isTopActivity(this, getPackageName(), SIMPLE_NAME)) {
                setOpenHintMsg();
            }
            isRestore = false;
        } else {
            setOpenHintMsg();
        }
    }

    private void setOpenHintMsg() {
        if (!isCleanBackRecord && getIsBackground()) {//
            DownloadService.setHint(BaseActivity.this);
        } else {
            DownloadService.clearHint();
        }
        putIsBackground(false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isRestore = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = VpnStoreApplication.getRefWatcher(this);
//        refWatcher.watch(this);
    }


    protected LoadAndErrorView getTipsView() {
        return mTipsView;
    }

    /**
     * 添加加载圈和错误提示view
     *
     * @param rootView 根view
     */
    protected void addLoadAndErrorView(ViewGroup rootView) {
        if (mTipsView == null) {
            mTipsView = new LoadAndErrorView(this);
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        if (rootView != null) {
            //先隐藏，在后面调用子控件各自的show时候显示出来
            mTipsView.setVisibility(View.GONE);
            rootView.addView(mTipsView, params);
            setListener();
        }
    }

    /**
     * 直接在XML中添加
     *
     * @param id XML中 LoadAndErrorView id
     */
    protected void setLoadAndErrorView(int id) {
        mTipsView = (LoadAndErrorView) this.findViewById(id);
        mTipsView.setVisibility(View.GONE);
        setListener();
    }

    private void setListener() {
        mTipsView.setOnReTryClickListener(new LoadAndErrorView.OnReTryClickListener() {
            @Override
            public void onRetry() {
                onReTryClicked();
            }
        });
        mTipsView.setOnRetryKeyListener(new LoadAndErrorView.OnRetryKeyListener() {
            @Override
            public boolean onRetryKey(View v, int keyCode, KeyEvent event) {
                return onRetryKeyClicked(v, keyCode, event);
            }
        });
    }

    public void showLoading() {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showLoading();
        }
    }

    public void showLoading(String loadDescribe) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showLoading(loadDescribe);
        }
    }

    public void hideLoading() {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.GONE);
            mTipsView.hideLoading();
        }
    }


    public void showOnlyErrorTips(String tips) {
        if (mTipsView != null) {
            mTipsView.showOnlyErrorTips(tips);
        }
    }

    public void showNetError(boolean retryRequestFocus) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showNetError(retryRequestFocus);
        }

    }

    /**
     * 请求错误，包含超时
     */
    public void showRequestError() {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showRequestError();
        }
    }

    protected void RetryTvRequestFocus() {
        if (mTipsView != null) {
            mTipsView.RetryTvRequestFocus();
        }
    }

    public void showRequestNoData(String tips) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showRequestNoData(tips);
        }
    }

    public void showOtherError(String tips) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showOtherError(tips);
        }
    }


    public void showOtherError(int errorCode) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showOtherError(getResources().getString(R.string.loading_error_retry)
                    + " [" + errorCode + "]");
        }
    }

    public void showError(int iconId, String tips, boolean isShowRetry) {
        if (mTipsView != null) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.showError(iconId, tips, isShowRetry);
        }
    }

    public void showError(int errorCode) {
        switch (errorCode) {
            case SubVolleyResponseHandler.REQUEST_FAIL:
                showNetError(true);
                break;
            case ReturnMessage.PARAM_ERROR:
                showOtherError(errorCode);
                break;
            case ReturnMessage.TIME_OFF:
                showRequestError();
                break;
            default:
                showOtherError(errorCode);
                break;
        }
    }

    public void showError(int errorCode, boolean retryRequestFocus) {
        switch (errorCode) {
            case SubVolleyResponseHandler.REQUEST_FAIL:
                showNetError(retryRequestFocus);
                break;
            case ReturnMessage.PARAM_ERROR:
                showOtherError(errorCode);
                break;
            case ReturnMessage.TIME_OFF:
                showRequestError();
                break;
            default:
                showOtherError(errorCode);
                break;
        }
    }

    protected void onReTryClicked() {

    }

    protected boolean onRetryKeyClicked(View v, int keyCode, KeyEvent event) {
        return false;
    }

    protected void updateLayout(final String layoutCode) {
        String savaStr = Pref.getString(Pref.LAYOUT_STRING, this, "");
        if (TextUtils.isEmpty(savaStr)) {
            ModeLevelVms.queryConfigForce(getApplicationContext(), 0, SIMPLE_NAME, new ModeUserErrorCode<ConfigInfo>() {
                @Override
                public void onJsonSuccess(ConfigInfo configInfo) {
                    if (configInfo != null) {
                        ConfigInfo.savaConfig(getApplicationContext(), configInfo);
                    } else {
                        LogDebugUtil.d(SIMPLE_NAME, "queryConfig onJsonSuccess: null");
                    }
                    queryLayout(layoutCode);
                }

                @Override
                public void onRequestFail(int code, Throwable e) {
                    queryLayout(layoutCode);
                }
            });
        } else {
            queryLayout(layoutCode);
        }
    }

    protected void queryLayout(final String layoutCode) {
//        InitLogic.queryLayout(getApplicationContext(), layoutCode, null);
    }

//    /**
//     * 获取用户新消息
//     */
//    protected void getUserNewReply() {
//        ModeLevelAmsNewReply.getNewReplyList(getApplicationContext(), SIMPLE_NAME);
//    }

}
 