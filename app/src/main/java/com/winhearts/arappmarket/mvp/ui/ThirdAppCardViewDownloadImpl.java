package com.winhearts.arappmarket.mvp.ui;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.view.PackageAppDownloadScrollbar;

/**
 * Description: 第三方app 打开
 * Created by lmh on 2016/3/28.
 */
public class ThirdAppCardViewDownloadImpl extends FrameLayout implements AppDownloadUi<ViewGroup> {
    private TextView nameTextView;
    private SimpleDraweeView icon;
    private PackageAppDownloadScrollbar scrollbar;
    private TextView installTextView;
    private AppDownloadPresenter presenter;


    public ThirdAppCardViewDownloadImpl(Context context) {
        super(context);
        init();
    }

    public ThirdAppCardViewDownloadImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThirdAppCardViewDownloadImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ThirdAppCardViewDownloadImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_packageapp_card, this, false);
        nameTextView = (TextView) view.findViewById(R.id.tv_packageapp_name);
        icon = (SimpleDraweeView) view.findViewById(R.id.sd_packageapp_download_icon);
        scrollbar = (PackageAppDownloadScrollbar) view.findViewById(R.id.progressBar_packageapp_bar);
        installTextView = (TextView) view.findViewById(R.id.btn_packageApp_install);
        setBtnShowInstall(false);
        addView(view);
    }

    public void setContent(String name, String url, String barText) {
        nameTextView.setText(name);
        if (!TextUtils.isEmpty(url)) {
            icon.setImageURI(Uri.parse(url));
        }

//        if (!TextUtils.isEmpty(barText)){
//            scrollbar.setText(barText);
//        }

    }

    public void setBarText(String barText) {
        scrollbar.setText(barText);
    }

    public void setBtnShowInstall(boolean isInstall) {
        if (isInstall) {
            installTextView.setText(getContext().getString(R.string.package_app_install));
        } else {
            installTextView.setText(getContext().getString(R.string.package_app_no_install));
        }
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public SimpleDraweeView getIcon() {
        return icon;
    }

    public PackageAppDownloadScrollbar getScrollbar() {
        return scrollbar;
    }

    public TextView getInstallTextView() {
        return installTextView;
    }

    @Override
    public ViewGroup getVar() {
        return this;
    }

    @Override
    public void showText(String title) {
        if (installTextView.getVisibility() != View.VISIBLE) {
            installTextView.setVisibility(View.VISIBLE);
        }
        if (scrollbar.getVisibility() == VISIBLE) {
            scrollbar.setVisibility(View.GONE);
        }
        installTextView.setText(title);
    }

    @Override
    public void showBarText(String title) {
        if (scrollbar.getVisibility() != View.VISIBLE) {
            scrollbar.setVisibility(View.VISIBLE);
        }
        if (installTextView.getVisibility() == View.VISIBLE) {
            installTextView.setVisibility(GONE);
        }
        scrollbar.setText(title);
    }

    @Override
    public void setBarMax(int max) {
        scrollbar.setMax(max);
    }

    @Override
    public void setBarProgress(int current) {
        if (scrollbar.getVisibility() != VISIBLE) {
            scrollbar.setVisibility(VISIBLE);
            installTextView.setVisibility(GONE);
        }

        scrollbar.setProgress(current);

    }


    @Override
    public void showCompletedInstall() {
        if (hasFocus()) {
            showText("打开");

        } else {
            showBarText("安装完成");
        }
    }

    @Override
    public ManageDownloadStatus getCompletedInstallStatus() {
        if (presenter != null) {
            return presenter.getCompletedInstallStatus();
        }
        return (ManageDownloadStatus) getTag(R.id.id_tag_download);
    }

    @Override
    public void setCompletedInstallStatus(ManageDownloadStatus status) {
        presenter.setCompletedInstallStatus(status);
    }


    @Override
    public void setPresenter(AppDownloadPresenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public AppDownloadPresenter getPresenter() {
        return presenter;
    }
}
