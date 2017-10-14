package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.winhearts.arappmarket.download.manage.ManagerReceiver;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.ResInfoEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.mvp.p.AppDownloadInstallCompletedListener;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenterImpl;
import com.winhearts.arappmarket.mvp.ui.ThirdAppCardViewDownloadImpl;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.ThirdAppOpenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description: 打开第三方app的弹框
 * Created by lmh on 2016/3/29.
 */
public class PackageAppOpenDialog extends Dialog {
    private String TAG = "PackageAppOpenDialog";

    private HorizontalLayout horizontalLayout;
    private ResInfoEntity resInfoEntity;
    private List<ResInfoEntity.AppsBean> apps;
    private LinearLayout layoutContentOut;
    private ManagerReceiver managerReceiver;
    private DownloadPath downloadPath;
    private boolean oneShoot = true;

    private List<AppDownloadPresenter> listUtils = new ArrayList<AppDownloadPresenter>();

    public PackageAppOpenDialog(Context context) {
        super(context, R.style.dialog);
        init();
    }

    public PackageAppOpenDialog(Context context, int theme) {
        super(context, R.style.dialog);
        init();
    }

    protected PackageAppOpenDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_packageapp_download, null, false);
        layoutContentOut = (LinearLayout) view.findViewById(R.id.layout_content_out);

        horizontalLayout = (HorizontalLayout) view.findViewById(R.id.verticalLayout_packageapp_dialog);

        setContentView(view);
    }


    public void simulate() {
        List<ResInfoEntity.AppsBean> apps = new ArrayList<ResInfoEntity.AppsBean>();
        ResInfoEntity.AppsBean
                bean = new ResInfoEntity.AppsBean();
        bean.setName("1");
        apps.add(bean);

        bean = new ResInfoEntity.AppsBean();
        bean.setName("2");
        apps.add(bean);

        bean = new ResInfoEntity.AppsBean();
        bean.setName("3");
        apps.add(bean);

        bean = new ResInfoEntity.AppsBean();
        bean.setName("4");
        apps.add(bean);

        bean = new ResInfoEntity.AppsBean();
        bean.setName("5");
        apps.add(bean);

        bean = new ResInfoEntity.AppsBean();
        bean.setName("6");
        apps.add(bean);

        setContent(apps);
    }


    public void setContent(List<ResInfoEntity.AppsBean> apps) {
        //apps = resInfoEntity.getApps();

        if (ContainerUtil.isEmptyOrNull(apps)) {
            return;
        }

        int width = 0;
        Context context = getContext();
        switch (apps.size()) {
            case 1:
                width = (int) context.getResources().getDimension(R.dimen.packageapp_cardview_width) * apps.size();
                width += (apps.size() + 1) * ScreenUtil.dip2px(getContext(), 67);
                break;

            case 2:
            case 3:
            case 4:
                width = (int) context.getResources().getDimension(R.dimen.packageapp_cardview_width) * apps.size();
                width += (apps.size() + 1) * ScreenUtil.dip2px(getContext(), 13);
                break;


            default:
                width = (int) context.getResources().getDimension(R.dimen.packageapp_cardview_width) * 4;
                width += (4 + 1) * ScreenUtil.dip2px(getContext(), 10);
                width += (int) context.getResources().getDimension(R.dimen.packageapp_cardview_width) * 0.6;
                horizontalLayout.setIsAutoScrollPage(false);
                break;
        }

        int cardViewHeight = (int) context.getResources().getDimension(R.dimen.packageapp_cardview_height);


        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutContentOut.getLayoutParams();
        lp.width = width;
        lp.height = cardViewHeight + ScreenUtil.dip2px(getContext(), 15);
        if (apps.size() == 1) {
//            lp.gravity = Gravity.CENTER_HORIZONTAL;

            layoutContentOut.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        layoutContentOut.requestLayout();

        int vwidth = (int) context.getResources().getDimension(R.dimen.packageapp_dialog_width_4);
        vwidth += ScreenUtil.dip2px(getContext(), 20);
        //1行4列
//        verticalLayout.setPadding();
        horizontalLayout.setSize(vwidth, cardViewHeight, 1, 4);
        horizontalLayout.blowUpUtil.isAnimationAble = false;
        horizontalLayout.blowUpUtil.setFocusDrawable(R.drawable.packageapp_item_download_dialog_p);
        VpnStoreApplication.updateRecordList();
        initContentLayoutCardView(apps);

    }

    private void initContentLayoutCardView(List<ResInfoEntity.AppsBean> apps) {
        int i = 0;
        for (ResInfoEntity.AppsBean bean : apps) {
            if (ManagerUtil.isPackageAppExist(getContext(), bean.getPackageName())) {
                bean.setInstallType(1);
            } else {
                bean.setInstallType(0);
            }
        }
        Collections.sort(apps, new Comparator<ResInfoEntity.AppsBean>() {
            public int compare(ResInfoEntity.AppsBean arg0, ResInfoEntity.AppsBean arg1) {
                return arg1.getInstallType() - arg0.getInstallType();
            }
        });

        for (ResInfoEntity.AppsBean bean : apps) {
            final ThirdAppCardViewDownloadImpl cardView = new ThirdAppCardViewDownloadImpl(getContext());
            initCardView(cardView, bean, i);

            i++;
        }

        horizontalLayout.focusFirstChildView();
    }

    private void initCardView(final ThirdAppCardViewDownloadImpl cardView, ResInfoEntity.AppsBean bean, int i) {
        cardView.setContent(bean.getName(), bean.getIcon(), "none");
        horizontalLayout.addSimpleItemView(cardView, i, 0, i + 1, 1, ScreenUtil.dip2px(getContext(), 10));

        setDownloadListener(cardView, bean);
        boolean isExist = ManagerUtil.isPackageAppExist(getContext(), bean.getPackageName());

        if (isExist) {
            cardView.setCompletedInstallStatus(ManageDownloadStatus.INSTALL_COMPLETE);
            cardView.showText("打开");
        } else {
            DownRecordInfo recordInfo = getRecord(bean.getPackageName());
            if (recordInfo != null) {
                cardView.setBarMax(recordInfo.getFileSize());
                cardView.setBarProgress(recordInfo.getDownlength());
                int state = recordInfo.getState();
                if (state == 1) {
                    cardView.showBarText("下载中");
                    cardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                } else {
                    cardView.setCompletedInstallStatus(ManageDownloadStatus.INITIAL);
                    cardView.showText("未安装");
                }
            } else {
                cardView.setCompletedInstallStatus(ManageDownloadStatus.INITIAL);
                cardView.showText("未安装");
            }
        }
    }

    public void setContent(String string) {
        resInfoEntity = new Gson().fromJson(string, ResInfoEntity.class);
        LogDebugUtil.d(TAG, "resInfoEntity = " + resInfoEntity.toString());
        if (resInfoEntity != null && ContainerUtil.isValid(resInfoEntity.getApps())) {
            setContent(resInfoEntity.getApps());
        }
    }

    public void setDownloadBundle(DownloadPath downloadPath) {
        this.downloadPath = downloadPath;
    }

    private DownRecordInfo getRecord(String mPackageName) {
        for (DownRecordInfo recordinfo : VpnStoreApplication.recordList) {
            if (recordinfo.getPackageName().equals(mPackageName)) {
                return recordinfo;
            }
        }
        return null;
    }

    private void setDownloadListener(final ThirdAppCardViewDownloadImpl cardView, final ResInfoEntity.AppsBean bean) {


        final AppDownloadPresenter presenter = new AppDownloadPresenterImpl(getContext(), cardView, bean.getName(), bean.getPackageName(), bean.getIcon());
        cardView.setPresenter(presenter);

        presenter.setOnInstallCompletedListener(new AppDownloadInstallCompletedListener() {
            @Override
            public void onCopmletedListener() {
                if (ContainerUtil.isValid(listUtils)) {
                    if (oneShoot) {
                        if (!ThirdAppOpenUtil.openApp(getContext(), bean)) {
                            ToastUtils.show(getContext(), bean.getName() + " 打开异常");
                            dismiss();
                        }
                        oneShoot = false;
                    }


                }
            }
        });
        listUtils.add(presenter);


        final View.OnFocusChangeListener listener = cardView.getOnFocusChangeListener();
        cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //恢复原来的逻辑
                listener.onFocusChange(v, hasFocus);
                ManageDownloadStatus status = (ManageDownloadStatus) cardView.getCompletedInstallStatus();
                if (hasFocus) {
                    switch (status) {
                        case INITIAL:


                            ManageDownloadStatus statusManage = presenter.getRealtimeStatus();
                            LogDebugUtil.d(TAG, "statusManage = " + statusManage.description);
                            switch (statusManage) {
                                case DOWNLOAD_LOADING:
                                case REQUEST_URL:
                                case REQUEST_URL_OK:
                                case TASK_CREATE:
                                case TASK_WAIT:


                                    break;

                                default:
                                    cardView.getInstallTextView().setSelected(true);
                                    cardView.showText("安装");
                                    cardView.getInstallTextView().setTextSize(20);
                                    break;
                            }
                            break;

                        case INSTALL_COMPLETE:
                            cardView.getInstallTextView().setSelected(true);
                            cardView.showText("打开");
                            cardView.getInstallTextView().setTextSize(20);
                            break;
                    }
                } else {
                    switch (status) {
                        case INITIAL:
                            ManageDownloadStatus statusManage = presenter.getRealtimeStatus();
                            switch (statusManage) {
                                case REQUEST_URL:
                                case REQUEST_URL_OK:
                                case DOWNLOAD_LOADING:
                                case DOWNLOAD_COMPLETE:
                                case DOWNLOAD_PAUSE:
                                case TASK_CREATE:
                                case TASK_WAIT:
                                    break;

                                default:
                                    cardView.getInstallTextView().setSelected(false);
                                    cardView.showText("未安装");
                                    cardView.getInstallTextView().setTextSize(16);
                                    break;
                            }

                            break;

                        case INSTALL_COMPLETE:
                            cardView.getInstallTextView().setSelected(false);
                            cardView.showText("打开");
                            cardView.getInstallTextView().setTextSize(20);
                            break;
                    }
                }

            }
        });

        cardView.getVar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageDownloadStatus status = cardView.getCompletedInstallStatus();
                switch (status) {
                    case INITIAL:

                        ManageDownloadStatus status1 = cardView.getPresenter().getRealtimeStatus();
                        if (status1 == ManageDownloadStatus.DOWNLOAD_LOADING) {
                            // presenter.pause();
                        } else {
                            //上报促成下载记录
                            if (downloadPath != null) {
                                downloadPath.setModulePath( bean.getPackageName());
                                ModeLevelAmsUpload.queryUploadDownPath(getContext(), downloadPath, new ModeUser<String>() {
                                    @Override
                                    public void onJsonSuccess(String s) {
                                    }

                                    @Override
                                    public void onRequestFail(Throwable e) {
                                    }
                                });
                            }
                            cardView.showBarText("下载中");
                            presenter.startAndGetUrl2Download();
                        }


                        break;

                    case INSTALL_COMPLETE:
                        if (!ThirdAppOpenUtil.openApp(getContext(), bean)) {
                            cardView.showText("打不开");
                        } else {
                            dismiss();
                        }


                        break;
                }

            }
        });


    }


    private boolean isAppExist(String packageName) {

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogDebugUtil.d(TAG, "onCreate(Bundle savedInstanceState)");
        Constant.DIALOG_SHOW = true;
        managerReceiver = new ManagerReceiver(getContext());
        managerReceiver.register();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        downloadPath = null;
        managerReceiver.unregister();
        LogDebugUtil.d(TAG, "dismiss()");
        Constant.DIALOG_SHOW = false;
        for (AppDownloadPresenter manager : listUtils) {
            manager.destroy();
        }
    }
}
