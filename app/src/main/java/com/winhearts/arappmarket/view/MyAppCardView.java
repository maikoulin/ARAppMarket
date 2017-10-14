package com.winhearts.arappmarket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * 我的应用子项
 * Created by lmh on 2016/8/22.
 */
public class MyAppCardView extends FrameLayout implements AppDownloadUi<ViewGroup> {

    private View menu;
    private TextView tvUpdate;
    private TextView tvComment;
    private TextView tvUninstall;
    private View downloadMenu;
    private TextView tvDownloadState;
    private TextView tvCancel;
    private PackageAppDownloadScrollbar downloadScrollbar;
    private AppDownloadPresenter presenter;
    private boolean isHeader = false;
    private String strState;
    private ImageView ivNew;

    public MyAppCardView(Context context) {
        super(context);
    }

    public MyAppCardView(Context context, boolean isHeader) {
        super(context);
        this.isHeader = isHeader;
        initView();
    }

    public MyAppCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAppCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        this.setFocusable(true);
        this.setClipChildren(false);
        LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int effect = ScreenUtil.dip2px(getContext(), 11);
        if (isHeader) {
            flp.setMargins(effect, 0, effect, 0);
        } else {
            flp.setMargins(effect, effect, effect, effect);
        }
        this.setLayoutParams(flp);
        LayoutInflater.from(getContext()).inflate(R.layout.adapter_myapp_item, this);
        menu = this.findViewById(R.id.app_card_menu);
        menu.setVisibility(GONE);
        tvUpdate = (TextView) this.findViewById(R.id.tv_app_card_update);
        tvUninstall = (TextView) this.findViewById(R.id.tv_app_card_uninstall);
        tvComment = (TextView) this.findViewById(R.id.tv_app_card_comment);
        downloadMenu = this.findViewById(R.id.app_card_download_menu);
        downloadMenu.setVisibility(GONE);
        tvDownloadState = (TextView) this.findViewById(R.id.tv_app_card_download_state);

        tvCancel = (TextView) this.findViewById(R.id.tv_app_card_download_cancel);

        downloadScrollbar = (PackageAppDownloadScrollbar) this.findViewById(R.id.ps_my_app_bar);
        ivNew = (ImageView) this.findViewById(R.id.iv_my_app_new);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                downMenu();
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (downBack()) {
                    return true;
                }
            }
            if (MainActivity.isShowMenu) {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                }
                if ((tvUpdate.isFocused() || tvDownloadState.isFocused()) && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (tvUpdate.getVisibility() != VISIBLE && tvComment.isFocused() && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if ((tvUninstall.isFocused() || tvCancel.isFocused()) && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void downMenu() {
        if (downloadMenu.getVisibility() == VISIBLE) {
            if (isHeader) {
                return;
            }
            MainActivity.isShowMenu = true;
            tvDownloadState.setVisibility(VISIBLE);
            tvCancel.setVisibility(VISIBLE);
            downloadScrollbar.setVisibility(GONE);
            tvDownloadState.requestFocus();
            tvDownloadState.setText(strState);
            return;
        }
        if (menu.getVisibility() != VISIBLE) {
            SoftwareInfo softwareInfo = (SoftwareInfo) this.getTag();
            if (softwareInfo != null && "1".equals(softwareInfo.getNeedUpdate())) {
                tvUpdate.setVisibility(VISIBLE);
            } else {
                tvUpdate.setVisibility(GONE);
            }
            MainActivity.isShowMenu = true;
            menu.setVisibility(VISIBLE);
            if (tvUpdate.getVisibility() == VISIBLE) {
                tvUpdate.requestFocus();
            } else {
                tvComment.requestFocus();
            }
        }
    }

    public boolean downBack() {

        if (downloadMenu != null && downloadMenu.getVisibility() == VISIBLE) {
            if (downloadScrollbar.getVisibility() != VISIBLE) {
                this.requestFocus();
                tvDownloadState.setVisibility(GONE);
                tvCancel.setVisibility(GONE);
                downloadScrollbar.setVisibility(VISIBLE);
                MainActivity.isShowMenu = false;
                return true;
            }

        }
        if (menu != null && menu.getVisibility() == VISIBLE) {
            this.requestFocus();
            menu.setVisibility(GONE);
            MainActivity.isShowMenu = false;
            return true;
        }
        return false;
    }

    @Override
    public ViewGroup getVar() {
        return this;
    }

    @Override
    public void showText(String tip) {
        if (!isHeader) {
            tvDownloadState.setText(tip);
            strState = tip;
        }
    }

    @Override
    public void showBarText(String barTip) {
        if (!isHeader) {
            downloadMenu.setVisibility(VISIBLE);
            downloadScrollbar.setText(barTip);
        }

    }

    @Override
    public void setBarMax(int max) {
        downloadScrollbar.setMax(max);

    }

    @Override
    public void setBarProgress(int current) {

        if (!MainActivity.isShowMenu) {
            downloadMenu.setVisibility(VISIBLE);
            if (isHeader) {
                downloadScrollbar.setVisibility(GONE);
            } else {
                downloadScrollbar.setVisibility(VISIBLE);
            }
            tvDownloadState.setVisibility(GONE);
            tvCancel.setVisibility(GONE);
        }
        downloadScrollbar.setProgress(current);


    }

    @Override
    public void showCompletedInstall() {
        if (downloadScrollbar.getVisibility() != VISIBLE && !isHeader) {
            MainActivity.isShowMenu = false;
            this.requestFocus();
        }
        ivNew.setVisibility(GONE);
        ivNew.setImageResource(R.drawable.myapp_bg_new_app);
        downloadMenu.setVisibility(GONE);
        setPresenter(null);
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
        if (presenter != null) {
            presenter.setCompletedInstallStatus(status);
        }
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