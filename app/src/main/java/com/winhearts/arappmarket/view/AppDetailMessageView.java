package com.winhearts.arappmarket.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenterImpl;
import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.activity.PicBrowseActivity;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.winhearts.arappmarket.wrapper.MyItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.utils.adapter.AppDetailPictureAdapter;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmh on 2016/4/28.
 */
public class AppDetailMessageView extends LinearLayout implements AppDownloadUi<ViewGroup>, View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvMoreSynopsis, tvSynopsis, tvDeveloper, tvDate, tvVersion, tvSize, tvDownloadAmount;
    private RecyclerView rvPicture;
    private ScoreView svScore;
    private SimpleDraweeView ivIcon;
    private TextView tvName, tvDeviceType;
    private TextView tvOpen, tvUpdate;
    private TextView tvDownLoad;
    private AppDetailDownloadScrollbar downloadScrollbar;
    private AppDownloadPresenter presenter;
    private DownloadPath downloadPath;
    private ImageView ivPicMore;

    private LinearLayoutManager pictureLayoutManager;
    private List<String> pictures = new ArrayList<>();
    private AppDetailPictureAdapter pictureAdapter = null;
    private SoftwareInfo softwareInfo;
    private DownRecordInfo record;
    private boolean isFocus = true;
    private boolean isUpdate = false;
    private boolean mIsCancel = false;

    public AppDetailMessageView(Context context) {
        super(context);
        initView();
    }

    public AppDetailMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AppDetailMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_app_detail_message, null);
        tvSynopsis = (TextView) view.findViewById(R.id.tv_app_detail_synopsis);
        tvDeveloper = (TextView) view.findViewById(R.id.tv_app_detail_developer);
        tvDate = (TextView) view.findViewById(R.id.tv_app_detail_date);
        tvVersion = (TextView) view.findViewById(R.id.tv_app_detail_version);
        tvSize = (TextView) view.findViewById(R.id.tv_app_detail_size);
        tvDownloadAmount = (TextView) view.findViewById(R.id.tv_app_detail_download_amount);
        svScore = (ScoreView) view.findViewById(R.id.sv_app_detail_score);
        ivIcon = (SimpleDraweeView) view.findViewById(R.id.iv_app_detail_icon);
        ivPicMore = (ImageView) view.findViewById(R.id.iv_app_detail_pic_more);

        tvName = (TextView) view.findViewById(R.id.tv_app_detail_name);
        tvDeviceType = (TextView) view.findViewById(R.id.tv_app_detail_device_type);

        tvOpen = (TextView) view.findViewById(R.id.tv_app_detail_open);
        tvOpen.setOnClickListener(this);
        tvUpdate = (TextView) view.findViewById(R.id.tv_app_detail_update);
        tvUpdate.setOnClickListener(this);
        tvDownLoad = (TextView) view.findViewById(R.id.tv_app_detail_download);
        tvDownLoad.setOnClickListener(this);
        downloadScrollbar = (AppDetailDownloadScrollbar) view.findViewById(R.id.progressBar_app_detail);
        downloadScrollbar.setOnClickListener(this);
        downloadScrollbar.setOnFocusChangeListener(this);

        rvPicture = (RecyclerView) view.findViewById(R.id.rv_app_detail_picture);
        rvPicture.setFocusable(false);
        rvPicture.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (pictureLayoutManager != null) {
                    int totalItemCount = pictureLayoutManager.getItemCount();
                    int lastVisibleItem = pictureLayoutManager.findLastVisibleItemPosition();
                    if (dy > 0) {
                        int visibleThreshold = 1;
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            ivPicMore.setVisibility(INVISIBLE);
                        } else {
                            ivPicMore.setVisibility(VISIBLE);
                        }
                    } else if (dy < 0) {
                        int visibleThreshold = 0;
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            ivPicMore.setVisibility(INVISIBLE);
                        } else {
                            ivPicMore.setVisibility(VISIBLE);
                        }
                    }
                }
            }
        });
        removeAllViews();
        addView(view);
    }

    public View getPictureView() {
        return rvPicture;
    }

    public void initDate(DownloadPath downloadPath, DownRecordInfo record) {
        isFocus = true;
        this.record = record;
        this.downloadPath = downloadPath;
        pictureAdapter = new AppDetailPictureAdapter(pictures);
        pictureLayoutManager = new LinearLayoutManager(getContext());
        pictureLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        rvPicture.setLayoutManager(pictureLayoutManager);
        rvPicture.setAdapter(pictureAdapter);

    }

    public void setMessage(SoftwareInfo response) {
        softwareInfo = response;
        downloadScrollbar.setProgress(0);
        ivIcon.setImageURI(Uri.parse(response.getIcon()));
        String previews = response.getPreviews();
        if (!TextUtils.isEmpty(previews)) {
            final String[] list = response.getPreviews().split(",");
            pictures.clear();
            pictures.addAll(Arrays.asList(list));
            if (pictures.size() > 2) {
                ivPicMore.setVisibility(VISIBLE);
            } else {
                ivPicMore.setVisibility(INVISIBLE);
            }
            pictureAdapter.notifyDataSetChanged();
            pictureAdapter.setOnItemClickListener(new MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), PicBrowseActivity.class);
                    intent.putExtra("urlList", list);
                    intent.putExtra("index", position);
                    getContext().startActivity(intent);
                }
            });
        }
        if (response.getDeviceType() != null) {
            String[] ctrlList = response.getDeviceType().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for (String aCtrlList : ctrlList) {
                switch (aCtrlList) {
                    case "TELECONTROL":
                        stringBuffer.append("/遥控");
                        break;
                    case "SOMATOSENSORY":
                        stringBuffer.append("/体感");
                        break;
                    case "HAND":
                        stringBuffer.append("/手柄");
                        break;
                    case "MICE":
                        stringBuffer.append("/鼠标");
                        break;
                }
            }
            if (stringBuffer.length() > 1) {
                stringBuffer.deleteCharAt(0);
            }
            tvDeviceType.setVisibility(View.VISIBLE);
            tvDeviceType.setText(stringBuffer);
        } else {
            tvDeviceType.setVisibility(View.INVISIBLE);
        }
        tvName.setText(response.getName());
        if (response.getDownload() != null) {
            tvDownloadAmount.setText(String.format("下载量： %s", response.getDownload()));
        } else {
            tvDownloadAmount.setText(String.format("下载量： %d", 0));
        }
        tvSize.setText(String.format("应用大小： %s", response.getSize()));
        tvVersion.setText(String.format("版本： %s", response.getVersionName().intern()));
        String updateTime = response.getUpdateTime();
        long time;
        if (TextUtils.isEmpty(updateTime)) {
            time = System.currentTimeMillis();
        } else {
            time = Long.valueOf(updateTime);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvDate.setText(String.format("更新日期： %s", sdf.format(new Date(time))));
        tvDeveloper.setText(String.format("开发者： %s", response.getDevelopor()));
        tvSynopsis.setText(response.getDescription());
        String star = response.getStar();
        if (star == null) {
            star = "0";
        }
        float score = Float.valueOf(star);
        try {
            svScore.setScore(score, false);
            svScore.setScoreNameVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppDownloadPresenter presenter = new AppDownloadPresenterImpl(getContext(), this, softwareInfo.getName(), softwareInfo.getPackageName(), softwareInfo.getIcon());
        setPresenter(presenter);
        if (record == null) {
            getState();
        } else {
            int state = record.getState();

            if (state == DownloadManager.CREATE_TASK || state == DownloadManager.WAIT) {
                setCompletedInstallStatus(ManageDownloadStatus.TASK_WAIT);
                setBarMax(record.getFileSize());
                setBarProgress(record.getDownlength());
                downloadScrollbar.requestFocus();
            } else if (state == DownloadManager.INSTALL_FAIL) {
                setCompletedInstallStatus(ManageDownloadStatus.INSTALL_NONE);
                showText("重试");
                tvDownLoad.requestFocus();
            } else {
                setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                setBarMax(record.getFileSize());
                setBarProgress(record.getDownlength());
                downloadScrollbar.requestFocus();
            }
        }
    }

    private void getState() {
        boolean isExist = ManagerUtil.isPackageAppExist(getContext(), softwareInfo.getPackageName());
        if (isExist) {
            setShowState();
        } else {
            setCompletedInstallStatus(ManageDownloadStatus.INITIAL);
            showText("下载");
            if (isFocus) {
                tvDownLoad.requestFocus();
                isFocus = false;
            }
        }
    }

    public void setShowState() {
        int versionCode = AppManager.getVersionCode(getContext(), softwareInfo.getPackageName());
        int vCode = Integer.valueOf(softwareInfo.getVersionCode());
        if (versionCode < vCode) {
            setCompletedInstallStatus(ManageDownloadStatus.INITIAL);
            tvUpdate.setVisibility(VISIBLE);
            tvOpen.setVisibility(VISIBLE);
            if (isFocus || downloadScrollbar.isFocused()) {
                tvUpdate.requestFocus();
                isFocus = false;
            }
            downloadScrollbar.setVisibility(GONE);
            tvDownLoad.setVisibility(GONE);
        } else {

            setCompletedInstallStatus(ManageDownloadStatus.INSTALL_COMPLETE);
            showText("打开");
            if (isFocus) {
                tvDownLoad.requestFocus();
                isFocus = false;
            }
        }
    }


    @Override
    public ViewGroup getVar() {
        return this;
    }

    @Override
    public void showText(String tip) {
        if (tvDownLoad.getVisibility() != VISIBLE) {
            tvDownLoad.setVisibility(VISIBLE);

            if (downloadScrollbar.isFocused()) {
                tvDownLoad.requestFocus();
            }
            downloadScrollbar.setVisibility(GONE);
            tvUpdate.setVisibility(GONE);
            tvOpen.setVisibility(GONE);
        }
        tvDownLoad.setText(tip);
    }

    @Override
    public void showBarText(String barTip) {
        if (mIsCancel) {
            return;
        }

        if (downloadScrollbar.getVisibility() != VISIBLE) {
            downloadScrollbar.setVisibility(VISIBLE);
            if (tvDownLoad.isFocused() || tvUpdate.isFocused()) {
                downloadScrollbar.requestFocus();
                downloadScrollbar.setProgressDrawable(getResources().getDrawable(R.drawable.app_detail_progress_horizontal_focus));
            }
            tvDownLoad.setVisibility(GONE);
            tvUpdate.setVisibility(GONE);
            tvOpen.setVisibility(GONE);
        }
        if (!downloadScrollbar.isFocused()) {
            downloadScrollbar.setText(barTip);
        }
    }

    @Override
    public void setBarMax(int max) {
        downloadScrollbar.setMax(max);
    }

    @Override
    public void setBarProgress(int current) {
        if (mIsCancel) {
            return;
        }
        if (downloadScrollbar.getVisibility() != VISIBLE) {
            downloadScrollbar.setVisibility(VISIBLE);
            if (tvDownLoad.isFocused()) {
                downloadScrollbar.requestFocus();
                downloadScrollbar.setProgressDrawable(getResources().getDrawable(R.drawable.app_detail_progress_horizontal_focus));
            }
            tvDownLoad.setVisibility(GONE);
            tvUpdate.setVisibility(GONE);
            tvOpen.setVisibility(GONE);
        }
        downloadScrollbar.setProgress(current);
    }

    @Override
    public void showCompletedInstall() {
        getState();
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

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        switch (viewId) {
            case R.id.tv_app_detail_download:
                mIsCancel = false;
                isUpdate = false;
                setDownLoadState();
                break;

            case R.id.progressBar_app_detail:
                setCancelDownload();
                mIsCancel = true;
                break;

            case R.id.tv_app_detail_open:
                openApp();
                break;

            case R.id.tv_app_detail_update:
                isUpdate = true;
                mIsCancel = false;
                setDownLoadState();
                break;
        }

    }


    private void setCancelDownload() {
        if (presenter != null) {
            presenter.cancelAndDelete();
            setBarProgress(0);
            getState();
        }
    }

    private void setDownLoadState() {
        ManageDownloadStatus status = this.getCompletedInstallStatus();
        switch (status) {
            case INITIAL:
                if (presenter != null && presenter.getRealtimeStatus() != ManageDownloadStatus.DOWNLOAD_LOADING) {
                    if (isStartDownload()) {
                        StartDownload();
                    } else {
                        ToastUtils.show(getContext(), "空间不足");
                    }
                }
                break;
            case INSTALL_COMPLETE:
                openApp();
                break;
            case INSTALL_NONE:
                DownRecordInfo record =
                        BasicDataInfo.getRecordBypackageName(
                                VpnStoreApplication.app.getSQLDatabase(), softwareInfo.getPackageName());
                if (record != null) {
                    DownloadManager.restart(getContext(), record.getDownloadUrl(), record.getPackageName());
                } else {
                    setCancelDownload();
                    ToastUtils.show(getContext(), "重试失败");
                }
                break;
        }
    }

    private boolean isStartDownload() {
        double memorySize;
        double apkSize;
        try {
            memorySize = 20 * 1024 + StorageUtils.getAvailableMemorySize() / 1024;
            String size = softwareInfo.getSize();
            String sizeNum = size.substring(0, size.length() - 2).trim();
            String unit = size.substring(size.length() - 2).toUpperCase();
            apkSize = Double.valueOf(sizeNum);
            switch (unit) {
                case "GB":
                    apkSize = apkSize * 1024 * 1024;
                    break;
                case "MB":
                    apkSize = apkSize * 1024;
                    break;
                case "KB":
                    break;
                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            LoggerUtil.i("NumberFormatException", e.getMessage());
            return true;
        }
        return memorySize > (apkSize * 1.3 + 100 * 1024);
    }

    private void StartDownload() {
        //上报促成下载记录
        if (downloadPath != null) {
            String modulePath = downloadPath.getModulePath();
            if (TextUtils.isEmpty(modulePath)) {
                downloadPath.setModulePath(softwareInfo.getPackageName());
            }

            ModeLevelAmsUpload.queryUploadDownPath(getContext(), downloadPath, new ModeUser<String>() {
                @Override
                public void onJsonSuccess(String s) {
                    downloadPath = null;
                }

                @Override
                public void onRequestFail(Throwable e) {
                    downloadPath = null;
                }
            });
        }
        this.showBarText("点击取消下载");
        presenter.startAndGetUrl2Download();
    }

    private void openApp() {
        String packageName = softwareInfo.getPackageName();
        ManagerUtil.startApk(getContext(), packageName);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        AppDownloadPresenter appDownloadPresenter = getPresenter();
        if (appDownloadPresenter == null) {
            return;
        }
        ManageDownloadStatus manageDownloadStatus = appDownloadPresenter.getRealtimeStatus();

        if (hasFocus) {
            if (manageDownloadStatus != ManageDownloadStatus.DOWNLOAD_COMPLETE) {
                downloadScrollbar.setText("点击取消下载");
            }
            downloadScrollbar.setProgressDrawable(getResources().getDrawable(R.drawable.app_detail_progress_horizontal_focus));
        } else {
            if (manageDownloadStatus != ManageDownloadStatus.DOWNLOAD_COMPLETE) {
                downloadScrollbar.setText("下载中");
            }
            downloadScrollbar.setProgressDrawable(getResources().getDrawable(R.drawable.app_detail_progress_horizontal_normal));

        }
    }

    private OnDownClickListener onDownClickListener;

    public void setOnDownClickListener(OnDownClickListener onDownClickListener) {
        this.onDownClickListener = onDownClickListener;
    }

    public interface OnDownClickListener {
        void downClick();
    }
}
