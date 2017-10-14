package com.winhearts.arappmarket.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * 填充页面
 * 1、加载页
 * 2、错误页
 * 3、空白页
 * Created by lmh on 2017/3/6.
 */


public class LoadAndErrorView extends FrameLayout {

    private TextView mLoadDescribeTv, mErrorDescribeTv;
    private TextView mRetryTv;
    private ImageView mTipsIcon;
    private View mProgressView;
    private View mErrorView;
    private OnReTryClickListener onReTryClickListener;
    private OnRetryKeyListener onRetryKeyListener;

    public LoadAndErrorView(Context context) {
        super(context);
        initView();
    }

    public LoadAndErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadAndErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public interface OnReTryClickListener {
        void onRetry();
    }

    public interface OnRetryKeyListener {
        boolean onRetryKey(View v, int keyCode, KeyEvent event);
    }

    public void setOnReTryClickListener(OnReTryClickListener onReTryClickListener) {
        this.onReTryClickListener = onReTryClickListener;
    }

    public void setOnRetryKeyListener(OnRetryKeyListener onRetryKeyListener) {
        this.onRetryKeyListener = onRetryKeyListener;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_load_and_error, this);
        mProgressView = this.findViewById(R.id.include_mode_load);
        mLoadDescribeTv = (TextView) this.findViewById(R.id.tv_progress_describe);
    }

    /**
     * 只显示加载圈，不显示提示
     */
    public void showLoading() {
        this.setVisibility(VISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        mLoadDescribeTv.setVisibility(GONE);
        if (mErrorView != null) {
            mErrorView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 显示加载圈，和提示
     *
     * @param loadDescribe 加载提示
     */
    public void showLoading(String loadDescribe) {
        setVisibility(VISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        mLoadDescribeTv.setVisibility(VISIBLE);
        mLoadDescribeTv.setText(loadDescribe);
        if (mErrorView != null) {
            mErrorView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 隐藏加载圈
     */
    public void hideLoading() {
        mProgressView.setVisibility(View.INVISIBLE);
        if (mErrorView != null) {
            mErrorView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 数据请求失败错误提示,网络问题
     */
    public void showNetError(boolean retryRequestFocus) {
        loadingTransformError();
        mTipsIcon.setVisibility(VISIBLE);
        mTipsIcon.setImageResource(R.drawable.common_network_error);
        mErrorDescribeTv.setText(R.string.connection_fail);
        mRetryTv.setVisibility(VISIBLE);
        if (retryRequestFocus) {
            mRetryTv.requestFocus();
        }
    }

    /**
     * 服务端请求错误，无响应
     */
    public void showRequestError() {
        loadingTransformError();
        mTipsIcon.setVisibility(VISIBLE);
        mTipsIcon.setImageResource(R.drawable.common_request_error);
        mErrorDescribeTv.setText(R.string.request_error);
        mRetryTv.setVisibility(VISIBLE);
        mRetryTv.requestFocus();
    }

    public void RetryTvRequestFocus() {
        if (mRetryTv != null && mRetryTv.getVisibility() == VISIBLE) {
            mRetryTv.requestFocus();
        }
    }

    /**
     * 服务端放回为空，无数据
     */
    public void showRequestNoData(String tips) {
        loadingTransformError();
        mTipsIcon.setVisibility(VISIBLE);
        mTipsIcon.setImageResource(R.drawable.common_request_error);
        if (TextUtils.isEmpty(tips)) {
            mErrorDescribeTv.setText(R.string.request_error);
        } else {
            mErrorDescribeTv.setText(tips);
        }
        mRetryTv.setVisibility(GONE);
    }

    public void showOtherError(String tips) {
        loadingTransformError();
        mTipsIcon.setVisibility(VISIBLE);
        mTipsIcon.setImageResource(R.drawable.common_request_error);
        mErrorDescribeTv.setText(tips);
        mRetryTv.setVisibility(VISIBLE);
        mRetryTv.requestFocus();
    }

    /**
     * 显示错误提示
     *
     * @param iconId      错误提示icon
     * @param tips        错误提示信息
     * @param isShowRetry 是否显示重试
     */
    public void showError(int iconId, String tips, boolean isShowRetry) {
        loadingTransformError();
        mTipsIcon.setVisibility(VISIBLE);
        mTipsIcon.setImageResource(iconId);
        mErrorDescribeTv.setText(tips);
        mRetryTv.setVisibility(isShowRetry ? VISIBLE : GONE);
        if (isShowRetry) {
            mRetryTv.requestFocus();
        }
    }

    /**
     * 错误提示，只显示错误信息
     *
     * @param tips 错误信息
     */
    public void showOnlyErrorTips(String tips) {
        loadingTransformError();
        mTipsIcon.setVisibility(GONE);
        mErrorDescribeTv.setText(tips);
        mRetryTv.setVisibility(GONE);
    }

    private void inflateErrorView() {
        if (mErrorView == null) {
            ViewStub viewStub = (ViewStub) this.findViewById(R.id.view_stub_mode_error);
            mErrorView = viewStub.inflate();
            mTipsIcon = (ImageView) mErrorView.findViewById(R.id.iv_error_icon);
            mErrorDescribeTv = (TextView) mErrorView.findViewById(R.id.tv_error_tips);
            mRetryTv = (TextView) mErrorView.findViewById(R.id.tv_error_retry);
            mRetryTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onReTryClickListener != null) {
                        onReTryClickListener.onRetry();
                    }
                }
            });
            mRetryTv.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return onRetryKeyListener != null && onRetryKeyListener.onRetryKey(v, keyCode, event);
                }
            });
        } else {
            mErrorView.setVisibility(VISIBLE);
        }
    }

    private void loadingTransformError() {
        mProgressView.setVisibility(View.INVISIBLE);
        inflateErrorView();
    }
}
