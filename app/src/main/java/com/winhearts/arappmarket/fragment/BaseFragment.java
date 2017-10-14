package com.winhearts.arappmarket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.model.ReturnMessage;
import com.winhearts.arappmarket.view.LoadAndErrorView;

/**
 * fragment基类
 */
public class BaseFragment extends Fragment {

    /**
     * rootView是否初始化标志，防止回调函数在rootView为空的时候触发
     */
    private boolean hasCreateView;

    /**
     * 当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
     */
    private boolean isFragmentVisible;

    /**
     * onCreateView()里返回的view，修饰为protected,所以子类继承该类时，在onCreateView里必须对该变量进行初始化
     */
    protected View rootView;

    protected boolean isFirstVisible;


    private boolean isOneShoot = false;
    private LoadAndErrorView mTipsView;
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isOneShoot && BuildConfig.MSYH){
//            isOneShoot = false;
//            FontManager.setActivityFont(this);
//        }
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView == null) {
            return;
        }

        if (isVisibleToUser && isFirstVisible) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        hasCreateView = true;
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = VpnStoreApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }


    private void initVariable() {
        hasCreateView = false;
        isFragmentVisible = false;
        isFirstVisible = true;
    }

    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    protected void onFragmentFirstVisible() {

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
            mTipsView = new LoadAndErrorView(getContext());
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
        mTipsView = (LoadAndErrorView) getActivity().findViewById(id);
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

    protected void RetryTvRequestFocus() {
        if (mTipsView != null) {
            mTipsView.RetryTvRequestFocus();
        }
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

    protected void onReTryClicked() {

    }

    protected boolean onRetryKeyClicked(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
