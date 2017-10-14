package com.winhearts.arappmarket.utils.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenterImpl;
import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.receiver.AppInstallReceiver;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.InstallPresEntity;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.BlowUpUtil;
import com.winhearts.arappmarket.view.MarqueeTextView;
import com.winhearts.arappmarket.view.MyAppCardView;
import com.winhearts.arappmarket.view.PackageAppDownloadScrollbar;
import com.winhearts.arappmarket.view.UninstallHintDialog;
import com.winhearts.arappmarket.view.UpdateHintDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的应用界面recycle适配器
 * Created by lmh on 2016/8/12.
 */
public class MyAppRecyclerAdapter extends BaseHeaderRecyclerAdapter<SoftwareInfo> implements View.OnClickListener {
    private BlowUpUtil blowUpUtil;
    private Context mContext;
    private String noOpenApps;
    private OnItemUpdateClickListener<SoftwareInfo> onItemUpdateClickListener;
    public List<AppDownloadPresenter> downloadList = new ArrayList<>();
    private int requestFocusPosition = -2;
    private boolean isHeader = false;
    private RecyclerView recyclerView;

    public MyAppRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
        blowUpUtil = new BlowUpUtil(mContext);
        blowUpUtil.setIsBringToFront(false);
        blowUpUtil.setFocusDrawable(R.drawable.comm_bg_card_focus);
        setNoOpenApps();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void isHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }


    public void setRequestFocusPosition(int requestFocusPosition) {
        this.requestFocusPosition = requestFocusPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = new MyAppCardView(parent.getContext(), isHeader);
        layout.setFocusable(true);
        return new MyHolder(layout);
    }

    @Override
    public int getItemCount() {
        if (isHeader) {
            return mDatas.size() <= 6 ? mDatas.size() : 6;
        }
        return super.getItemCount();
    }

    @Override
    public void onBind(final RecyclerView.ViewHolder viewHolder, final int RealPosition, final SoftwareInfo data) {
        if (viewHolder instanceof MyHolder) {
            viewHolder.itemView.setTag(data);
            AppDownloadPresenter presenter = ((MyAppCardView) viewHolder.itemView).getPresenter();
            if (presenter != null) {
                presenter.destroy();
                downloadList.remove(presenter);
                ((MyAppCardView) viewHolder.itemView).setPresenter(null);
            }
            ((MyHolder) viewHolder).tvUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.isShowMenu = false;
                    if (onItemUpdateClickListener != null) {
                        onItemUpdateClickListener.onItemUpdateClick(viewHolder, RealPosition, data);
                    }
                }
            });
            ((MyHolder) viewHolder).menu.setVisibility(View.GONE);
            ((MyHolder) viewHolder).downloadMenu.setVisibility(View.GONE);
            viewHolder.itemView.setBackgroundResource(R.color.transparent);

            ((MyHolder) viewHolder).tvDownloadState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyAppCardView) viewHolder.itemView).downBack();
                    setDownloadState(RealPosition, data);
                }
            });
            ((MyHolder) viewHolder).tvComment.setTag(RealPosition);
            ((MyHolder) viewHolder).tvUninstall.setOnClickListener(this);
            ((MyHolder) viewHolder).tvUninstall.setTag(RealPosition);
            ((MyHolder) viewHolder).tvDownloadState.setTag(RealPosition);
            ((MyHolder) viewHolder).tvCancel.setOnClickListener(this);
            ((MyHolder) viewHolder).tvCancel.setTag(RealPosition);
            if (data.getIcon() != null) {
                CommonHierarchy.showAppIcon(mContext, Uri.parse(data.getIcon()), ((MyHolder) viewHolder).ivApp);
            } else {
                GenericDraweeHierarchy hierarchy = ((MyHolder) viewHolder).ivApp.getHierarchy();
                ((MyHolder) viewHolder).ivApp.setImageURI("");
                Drawable drawable;
                try {
                    drawable = AppManager.getAppIcon(mContext,
                            data.getPackageName());

                    if (drawable != null) {
                        hierarchy.setPlaceholderImage(drawable, ScalingUtils.ScaleType.FIT_XY);
                    } else {
                        hierarchy.setPlaceholderImage(R.drawable.background_app_icon, ScalingUtils.ScaleType.FIT_XY);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    hierarchy.setPlaceholderImage(R.drawable.background_app_icon, ScalingUtils.ScaleType.FIT_XY);
                    e.printStackTrace();
                }

            }
            String packageName = data.getPackageName();

            if (!TextUtils.isEmpty(noOpenApps) && noOpenApps.contains(packageName)) {
                ((MyHolder) viewHolder).ivNew.setVisibility(View.VISIBLE);
                ((MyHolder) viewHolder).ivNew.setImageResource(R.drawable.myapp_bg_new_app);
            } else {
                ((MyHolder) viewHolder).ivNew.setVisibility(View.INVISIBLE);
            }
            DownRecordInfo downRecordInfo = data.getDownRecordInfo();
            if (downRecordInfo != null) {
                ((MyHolder) viewHolder).downloadMenu.setVisibility(View.VISIBLE);
                ((MyHolder) viewHolder).tvDownloadState.setVisibility(View.GONE);
                ((MyHolder) viewHolder).tvCancel.setVisibility(View.GONE);
                if (isHeader) {
                    ((MyHolder) viewHolder).downloadScrollbar.setVisibility(View.GONE);
                } else {
                    ((MyHolder) viewHolder).downloadScrollbar.setVisibility(View.VISIBLE);
                    setPresenterRelate(RealPosition, (AppDownloadUi<ViewGroup>) viewHolder.itemView, data);
                }
            }
            if ("1".equals(data.getNeedUpdate())) {
                ((MyHolder) viewHolder).ivNew.setVisibility(View.VISIBLE);
                ((MyHolder) viewHolder).ivNew.setImageResource(R.drawable.app_main_update);
                ((MyHolder) viewHolder).tvUpdate.setVisibility(View.VISIBLE);
                if (downRecordInfo == null) {
                    setPresenterRelate(RealPosition, (AppDownloadUi<ViewGroup>) viewHolder.itemView, data);
                }
            }
            ((MyHolder) viewHolder).tvApp.setText(data.getName());
            ((MyHolder) viewHolder).itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!MainActivity.isShowMenu) {
                        if (hasFocus) {
                            blowUpUtil.setScaleUp(v);
                        } else {
                            blowUpUtil.setScaleDown(v);
                        }
                    }
                }
            });
        }
        if (RealPosition == requestFocusPosition) {
            viewHolder.itemView.requestFocus();
            requestFocusPosition = -2;
        }
    }

    @Override
    public void onHeadBind(RecyclerView.ViewHolder viewHolder) {

    }

    public void setNoOpenApps() {
        this.noOpenApps = PrefNormalUtils.getString(mContext, PrefNormalUtils.ALL_NO_OPEN_APP, null);
    }

    public BlowUpUtil getBlowUpUtil() {
        return blowUpUtil;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int realPosition = (int) v.getTag();
        SoftwareInfo softwareInfo = mDatas.get(realPosition);
        switch (id) {
            case R.id.tv_app_card_uninstall:
                uninstall(softwareInfo);
                break;
            case R.id.tv_app_card_download_cancel:
                setCancel(realPosition, softwareInfo);
                break;
        }

    }

    private void setCancel(int realPosition, SoftwareInfo softwareInfo) {
        DownRecordInfo downRecordInfo1 = softwareInfo.getDownRecordInfo();
        if (downRecordInfo1 != null) {
            MainActivity.isShowMenu = false;
            MyAppCardView myAppCardView = (MyAppCardView) recyclerView.getChildAt(realPosition);
            AppDownloadPresenter presenter = myAppCardView.getPresenter();
            if (presenter != null) {
                presenter.cancelAndDelete();
                presenter.destroy();
                downloadList.remove(presenter);
            }

            if (ManagerUtil.checkApplication(mContext, softwareInfo.getPackageName())) {
                MainActivity.isShowMenu = false;
            } else {
                mDatas.remove(softwareInfo);
                this.notifyItemRemoved(realPosition);
                this.setRequestFocusPosition(realPosition);
                this.notifyItemRangeChanged(realPosition, getItemCount() - realPosition - 1);
            }
        } else {
            ToastUtils.show(mContext, "取消失败");
        }
    }

    private void setDownloadState(int realPosition, SoftwareInfo softwareInfo) {

        DownRecordInfo record = BasicDataInfo.getRecordBypackageName(
                VpnStoreApplication.app.getSQLDatabase(), softwareInfo.getPackageName());

        if (record != null && recyclerView != null) {
            MyAppCardView myAppCardView = (MyAppCardView) recyclerView.getChildAt(realPosition);
            softwareInfo.setDownRecordInfo(record);
            if (record.getState() == DownloadManager.LOADING) {
                DownloadManager.pause(mContext, record.getDownloadUrl(),
                        record.getPackageName());
                myAppCardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_PAUSE);
                myAppCardView.showText("继续");
                myAppCardView.showBarText("暂停下载");
                record.setState(DownloadManager.PAUSE);
            } else if (record.getState() == DownloadManager.INSTALL_FAIL &&
                    myAppCardView.getCompletedInstallStatus() == ManageDownloadStatus.INSTALL_NONE) {
                myAppCardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                myAppCardView.showText("暂停");
                myAppCardView.showBarText("等待下载");
                DownloadManager.restart(mContext, record.getDownloadUrl(), record.getPackageName());
                record.setState(DownloadManager.LOADING);
            } else {

                myAppCardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                myAppCardView.showText("暂停");
                myAppCardView.showBarText("等待下载");
                DownloadManager.start(mContext, record.getDownloadUrl(),
                        record.getPackageName());
                record.setState(DownloadManager.LOADING);
            }
            softwareInfo.setDownRecordInfo(record);
        } else {
            ToastUtils.show(mContext, "操作失败");
        }
    }


    private void uninstall(SoftwareInfo softwareInfo) {
        InstallPresEntity entity = getPresEntity(mContext);
        if (entity != null && entity.getPres() != null
                && !entity.getPres().isEmpty()) {
            for (InstallPresEntity.Element element : entity.getPres()) {
                if (element.getPackageName().equals(softwareInfo.getPackageName())) {
                    if (element.getPreType().equals("DISABLE_UNINSTALL")) {
                        showPresApp(softwareInfo);
                        return;
                    }
                }
            }
            //手动卸载
            AppInstallReceiver.handUninstallAppName = softwareInfo.getPackageName();
            Uri uri = Uri.parse("package:"
                    + softwareInfo.getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE,
                    uri);
            mContext.startActivity(intent);
        } else {
            AppInstallReceiver.handUninstallAppName = softwareInfo.getPackageName();
            Uri uri = Uri.parse("package:"
                    + softwareInfo.getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE,
                    uri);
            mContext.startActivity(intent);
        }
    }

    /**
     * 获取预安装列表
     *
     * @param context
     * @return
     */
    public InstallPresEntity getPresEntity(Context context) {
        SharedPreferences preference = context.getSharedPreferences("installPres", Activity.MODE_PRIVATE);
        String content = preference.getString("installPres", null);
        InstallPresEntity installPres = null;
        if (!TextUtils.isEmpty(content)) {
            Type type = new TypeToken<InstallPresEntity>() {
            }.getType();
            installPres = new Gson().fromJson(content, type);
        }
        return installPres;
    }

    private void showPresApp(final SoftwareInfo softwareInfo) {

        final UninstallHintDialog dialog = new UninstallHintDialog(
                mContext,
                UpdateHintDialog.Type_Lastest_Version,
                softwareInfo.getName(),
                softwareInfo.getPackageName());
        dialog.setConfirmButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getTvHint().setText("系统预安装应用，无法卸载");
        if (mContext != null) {
            dialog.show();
        }
    }


    public void setOnItemUpdateClickListener(OnItemUpdateClickListener<SoftwareInfo> onItemUpdateClickListener) {
        this.onItemUpdateClickListener = onItemUpdateClickListener;
    }

    public interface OnItemUpdateClickListener<T> {
        void onItemUpdateClick(RecyclerView.ViewHolder viewHolder, int position, T data);
    }

    private class MyHolder extends BaseHeaderRecyclerAdapter.Holder {
        private SimpleDraweeView ivApp;
        private MarqueeTextView tvApp;
        private ImageView ivNew;

        private View menu;
        private View downloadMenu;
        private TextView tvUpdate;
        private TextView tvComment;
        private TextView tvUninstall;

        private TextView tvDownloadState;
        private TextView tvCancel;
        private PackageAppDownloadScrollbar downloadScrollbar;


        MyHolder(View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.app_card_menu);
            downloadMenu = itemView.findViewById(R.id.app_card_download_menu);
            ivApp = (SimpleDraweeView) itemView.findViewById(R.id.rv_app_card_icon);
            tvApp = (MarqueeTextView) itemView.findViewById(R.id.tv_app_card_name);
            ivNew = (ImageView) itemView.findViewById(R.id.iv_my_app_new);
            tvUpdate = (TextView) itemView.findViewById(R.id.tv_app_card_update);
            tvComment = (TextView) itemView.findViewById(R.id.tv_app_card_comment);
            tvUninstall = (TextView) itemView.findViewById(R.id.tv_app_card_uninstall);
            tvDownloadState = (TextView) itemView.findViewById(R.id.tv_app_card_download_state);
            tvCancel = (TextView) itemView.findViewById(R.id.tv_app_card_download_cancel);
            downloadScrollbar = (PackageAppDownloadScrollbar) itemView.findViewById(R.id.ps_my_app_bar);
        }
    }

    private void setPresenterRelate(int RealPosition, AppDownloadUi<ViewGroup> cardView, SoftwareInfo item) {
        AppDownloadPresenter presenter = new AppDownloadPresenterImpl(mContext, cardView, item.getName(), item.getPackageName(), item.getIcon());
        cardView.setPresenter(presenter);
        DownRecordInfo recordInfo = item.getDownRecordInfo();
        if (recordInfo != null) {
            if (TextUtils.isEmpty(recordInfo.getDownloadUrl())) {
                presenter.startAndGetUrl2Download();
            }
            cardView.setBarMax(recordInfo.getFileSize());
            cardView.setBarProgress(recordInfo.getDownlength());
            int state = recordInfo.getState();
            if (state == DownloadManager.PAUSE) {
                cardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_PAUSE);
                cardView.showBarText("暂停下载");
                cardView.showText("继续");
            } else if (state == DownloadManager.CREATE_TASK || state == DownloadManager.WAIT) {
                cardView.setCompletedInstallStatus(ManageDownloadStatus.TASK_WAIT);
                cardView.showBarText("等待下载");
                cardView.showText("暂停");
            } else if (state == DownloadManager.INSTALL_FAIL) {
                cardView.setCompletedInstallStatus(ManageDownloadStatus.INSTALL_NONE);
                cardView.showBarText("安装失败");
                cardView.showText("重试");
            } else {
                cardView.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_LOADING);
                cardView.showBarText("下载中");
                cardView.showText("暂停");
            }
        }
        downloadList.add(presenter);
    }
}
