package com.winhearts.arappmarket.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenterImpl;
import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;

/**
 * 专题子项
 * Created by lmh on 2016/3/30.
 */
public class TopicCardView extends FrameLayout implements AppDownloadUi<ViewGroup>, View.OnClickListener {
    private SimpleDraweeView sdIcon;
    private MarqueeTextView tvName;
    private TextView tvSize, tvDownload;
    private ScoreView scoreView;
    private View downloadMenu;
    private TextView tvDownloadState;
    private TextView tvCancel;
    private PackageAppDownloadScrollbar downloadScrollbar;
    private boolean isShowMenu = false;
    private String strState;
    private AppDownloadPresenter presenter;
    private SoftwareInfo softwareInfo;

    public TopicCardView(Context context) {
        super(context);
        initView();
    }

    public TopicCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TopicCardView bindData(SoftwareInfo softwareInfo) {

        if (softwareInfo.getIcon() != null) {
            sdIcon.setImageURI(Uri.parse(softwareInfo.getIcon()));
        }
        tvName.setText(softwareInfo.getName());
        String star = softwareInfo.getStar();
        float score;
        if (TextUtils.isEmpty(star)) {
            score = 5;
        } else {
            score = Float.valueOf(star);
        }
        try {
            scoreView.setScore(score, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvSize.setText("大小 ：" + softwareInfo.getSize());
        tvDownload.setText("下载量 : " + softwareInfo.getDownload());
        return this;
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_topic_card, this);
        sdIcon = (SimpleDraweeView) view.findViewById(R.id.sd_topic_cart_icon);
        scoreView = (ScoreView) view.findViewById(R.id.sv_topic_cart_star);
        tvName = (MarqueeTextView) view.findViewById(R.id.tv_topic_cart_name);
        tvSize = (TextView) view.findViewById(R.id.tv_topic_cart_size);
        tvDownload = (TextView) view.findViewById(R.id.tv_topic_cart_download);
        sdIcon.setHierarchy(CommonHierarchy.setHierarchyAppIcon(sdIcon));
        downloadMenu = this.findViewById(R.id.ll_topic_card_download_menu);
        downloadMenu.setVisibility(GONE);
        tvDownloadState = (TextView) this.findViewById(R.id.tv_topic_card_download_state);
        tvDownloadState.setOnClickListener(this);
        tvCancel = (TextView) this.findViewById(R.id.tv_topic_card_download_cancel);
        tvCancel.setOnClickListener(this);
        downloadScrollbar = (PackageAppDownloadScrollbar) this.findViewById(R.id.ps_topic_card_bar);
    }

    public void setMarquee(boolean hasFocus) {
        tvName.setMarquee(hasFocus);
    }

    private void setDownload() {
        if (softwareInfo != null) {
            boolean isExist = ManagerUtil.isPackageAppExist(getContext(), softwareInfo.getPackageName());
            if (isExist) {
                ToastUtils.show(getContext(), "应用已安装");
            } else {
                if (getPresenter() == null) {
                    AppDownloadPresenter presenter = new AppDownloadPresenterImpl(getContext(), this, softwareInfo.getName(), softwareInfo.getPackageName(), softwareInfo.getIcon());
                    setPresenter(presenter);
                    setCompletedInstallStatus(ManageDownloadStatus.TASK_WAIT);
                    showBarText("等待下载");
                    showText("暂停");
                    setBarProgress(0);
                    presenter.startAndGetUrl2Download();
                }
                this.requestFocus();
            }
        } else {
            ToastUtils.show(getContext(), "应用无法下载");
        }

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
            if (isShowMenu) {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                }
                if (tvDownloadState.isFocused() && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (tvCancel.isFocused() && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
            } else {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    setDownload();
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public boolean downMenu() {
        if (downloadMenu.getVisibility() == VISIBLE) {
            isShowMenu = true;
            tvDownloadState.setVisibility(VISIBLE);
            tvCancel.setVisibility(VISIBLE);
            downloadScrollbar.setVisibility(GONE);
            tvDownloadState.requestFocus();
            tvDownloadState.setText(strState);
            return true;
        }
        return false;
    }

    public boolean downBack() {
        if (downloadMenu != null && downloadMenu.getVisibility() == VISIBLE) {
            if (downloadScrollbar.getVisibility() != VISIBLE) {
                this.requestFocus();
                tvDownloadState.setVisibility(GONE);
                tvCancel.setVisibility(GONE);
                downloadScrollbar.setVisibility(VISIBLE);
                isShowMenu = false;
                return true;
            }

        }
        return false;
    }

    @Override
    public ViewGroup getVar() {
        return this;
    }

    @Override
    public void showText(String tip) {
        tvDownloadState.setText(tip);
        strState = tip;
    }

    @Override
    public void showBarText(String barTip) {
        downloadMenu.setVisibility(VISIBLE);
        downloadScrollbar.setText(barTip);

    }

    @Override
    public void setBarMax(int max) {
        downloadScrollbar.setMax(max);
    }

    @Override
    public void setBarProgress(int current) {
        if (!isShowMenu) {
            downloadMenu.setVisibility(VISIBLE);
            downloadScrollbar.setVisibility(VISIBLE);
            tvDownloadState.setVisibility(GONE);
            tvCancel.setVisibility(GONE);
        }
        downloadScrollbar.setProgress(current);
    }

    @Override
    public void showCompletedInstall() {
        if (downloadScrollbar.getVisibility() != VISIBLE) {
            isShowMenu = false;
            this.requestFocus();
        }
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.tv_topic_card_download_state:
                downBack();
                setDownloadState(softwareInfo);
                break;
            case R.id.tv_topic_card_download_cancel:
                if (presenter != null) {
                    presenter.cancelAndDelete();
                    downloadScrollbar.setProgress(0);
                    presenter.destroy();
                    presenter = null;
                    downloadMenu.setVisibility(GONE);
                    isShowMenu = false;
                    this.requestFocus();
                }
                break;
        }
    }

    private void setDownloadState(SoftwareInfo softwareInfo) {
        DownRecordInfo record = BasicDataInfo.getRecordBypackageName(
                VpnStoreApplication.app.getSQLDatabase(), softwareInfo.getPackageName());
        if (record != null) {
            softwareInfo.setDownRecordInfo(record);
            if (record.getState() == DownloadManager.LOADING) {
                DownloadManager.pause(getContext(), record.getDownloadUrl(),
                        record.getPackageName());
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_PAUSE);
                showText("继续");
                showBarText("暂停下载");
                record.setState(DownloadManager.PAUSE);
            } else if (record.getState() == DownloadManager.INSTALL_FAIL &&
                    getCompletedInstallStatus() == ManageDownloadStatus.INSTALL_NONE) {
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                showText("暂停");
                showBarText("等待下载");
                DownloadManager.restart(getContext(), record.getDownloadUrl(), record.getPackageName());
                record.setState(DownloadManager.LOADING);
            } else {
//                if (getCompletedInstallStatus() == ManageDownloadStatus.DOWNLOAD_FAIL
//                        || getCompletedInstallStatus() == ManageDownloadStatus.TASK_CREATE_FAIL) {
//                }
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                showText("暂停");
                showBarText("等待下载");
                DownloadManager.start(getContext(), record.getDownloadUrl(),
                        record.getPackageName());
                record.setState(DownloadManager.LOADING);
            }
            softwareInfo.setDownRecordInfo(record);
        } else {
            ToastUtils.show(getContext(), "操作失败");
        }
    }

    public void setPresenterRelate(SoftwareInfo softwareInfo) {
        this.softwareInfo = softwareInfo;
        DownRecordInfo recordInfo = BasicDataInfo.getRecordBypackageName(
                VpnStoreApplication.app.getSQLDatabase(), softwareInfo.getPackageName());
        if (recordInfo != null) {
            softwareInfo.setDownRecordInfo(recordInfo);
            AppDownloadPresenter presenter = new AppDownloadPresenterImpl(getContext(), this, softwareInfo.getName(), softwareInfo.getPackageName(), softwareInfo.getIcon());
            setPresenter(presenter);
            setBarMax(recordInfo.getFileSize());
            setBarProgress(recordInfo.getDownlength());
            int state = recordInfo.getState();
            if (state == DownloadManager.PAUSE) {
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_PAUSE);
                showBarText("暂停下载");
                showText("继续");
            } else if (state == DownloadManager.CREATE_TASK || state == DownloadManager.WAIT) {
                setCompletedInstallStatus(ManageDownloadStatus.TASK_WAIT);
                showBarText("等待下载");
                showText("暂停");
            } else if (state == DownloadManager.INSTALL_FAIL) {
                setCompletedInstallStatus(ManageDownloadStatus.INSTALL_NONE);
                showBarText("安装失败");
                showText("重试");
            } else {
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                showBarText("下载中");
                showText("暂停");
            }
        } else {
            downloadMenu.setVisibility(GONE);
        }
    }

    public boolean getIsShowMenu() {
        return isShowMenu;
    }
}