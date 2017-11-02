package com.winhearts.arappmarket.fragment;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.TextView;

import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.manage.ManagerReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.utils.adapter.BaseHeaderRecyclerAdapter;
import com.winhearts.arappmarket.utils.adapter.MyAppRecyclerAdapter;
import com.winhearts.arappmarket.event.OpenAPkEvent;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.PackageList;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsInstall;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.MyAPPGridItemDecoration;
import com.winhearts.arappmarket.view.MyAppCardView;
import com.winhearts.arappmarket.view.MyAppGridLayoutManager;
import com.winhearts.arappmarket.view.BorderRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * 我的应用
 * Created by lmh on 2016/8/11.
 */
public class MyAppFragment extends MovableFragment implements BorderRecyclerView.BorderListener {
    private final static String TAG = "MyAppFragment";
    private boolean isDebug = false;
    private ArrayList<SoftwareInfo> mApps = new ArrayList<>(); // 显示的应用 。
    private ArrayList<SoftwareInfo> mWhiteApps = new ArrayList<>(); // 平台已安装应用
    private ArrayList<SoftwareInfo> mOtherApps = new ArrayList<>(); //非平台已安装应用
    private ArrayList<SoftwareInfo> downloadApps = new ArrayList<>(); //需要更新但还没更新的应用

    private BorderRecyclerView mRecyclerView;
    private View more;
    private GridLayoutManager mLayoutManager;
    private MyAppRecyclerAdapter mAdapter;
    private Context mContext;
    private int show_other_app = 1;
    private ManagerReceiver managerReceiver;
    private CompositeSubscription compositeSubscription;
    private boolean isShow = false;
    private AlertDialog alertDialog = null;
    private TextView mLeftMenuTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction("android.intent.action.PACKAGE_ADDED");
        myFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        myFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        myFilter.addDataScheme("package");
        getActivity().registerReceiver(myUpdateMyApp, myFilter);
        try {
            show_other_app = Integer.parseInt(Pref.getString(Pref.SHOW_OTHER_APP_SWITCH, getActivity(), "1"));
        } catch (Exception e) {
            show_other_app = 1;
        }
        managerReceiver = new ManagerReceiver(getActivity());
        managerReceiver.register();
        compositeSubscription = new CompositeSubscription();
        mAdapter = new MyAppRecyclerAdapter(getActivity());
        VpnStoreApplication.updateRecordList();
        initEvent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myapp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (BorderRecyclerView) view.findViewById(R.id.rv_my_app_content);

        more = view.findViewById(R.id.iv_my_app_more);
        initFuncItem(view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new MyAppGridLayoutManager(getActivity(), 6);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setRecyclerView(mRecyclerView);
        mRecyclerView.setBorderListener(this);
        mRecyclerView.addItemDecoration(new MyAPPGridItemDecoration(getActivity(), false));
        mAdapter.addDatas(getDatas());
        setMoreVisibility();
        setItemListener();

    }

    private void initFuncItem(View view) {
        mLeftMenuTv = (TextView) view.findViewById(R.id.tv_left_menu_hint);
        boolean isOpen = PrefNormalUtils.getBoolean("leftMenuOpen", false);
        if (isOpen) {
            mLeftMenuTv.setVisibility(View.INVISIBLE);
        } else {
            mLeftMenuTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        isShow = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        isShow = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        managerReceiver.unregister();
        mContext.unregisterReceiver(myUpdateMyApp);
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
        for (AppDownloadPresenter download : mAdapter.downloadList) {
            download.destroy();
        }
        super.onDestroy();
    }

    private void setMoreVisibility() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0) {
                    initMoreVisibility();
                }
                if (mLayoutManager != null) {
                    int totalItemCount = mLayoutManager.getItemCount();
                    int lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (dy > 0) {
                        int visibleThreshold = 1;
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            more.setVisibility(View.INVISIBLE);
                        } else {
                            more.setVisibility(View.VISIBLE);
                        }
                    } else if (dy < 0) {
                        int visibleThreshold = 0;
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            more.setVisibility(View.INVISIBLE);
                        } else {
                            more.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void initMoreVisibility() {
        if (mApps.size() > 12) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.INVISIBLE);
        }
    }

    private void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private void setItemListener() {
        mAdapter.setOnItemClickListener(new BaseHeaderRecyclerAdapter.OnItemClickListener<SoftwareInfo>() {
            @Override
            public void onItemClick(View view, int position, SoftwareInfo data) {
                if (!"1".equals(data.getNeedUpdate())) {
                    view.findViewById(R.id.iv_my_app_new).setVisibility(View.INVISIBLE);
                }
                String packageName = data.getPackageName();
                for (DownRecordInfo downRecordInfo : VpnStoreApplication.recordList) {
                    if (packageName.equals(downRecordInfo.getPackageName())) {
                        ((MyAppCardView) view).downMenu();
//                        ToastUtils.show(mContext, "应用正在下载,无法打开，请稍等...");
                        return;
                    }
                }
                ManagerUtil.startApk(mContext, packageName);
            }
        });

        mAdapter.setOnItemUpdateClickListener(new MyAppRecyclerAdapter.OnItemUpdateClickListener<SoftwareInfo>() {
            @Override
            public void onItemUpdateClick(RecyclerView.ViewHolder viewHolder, int position, SoftwareInfo data) {
                startUpdate(data, position, (AppDownloadUi<ViewGroup>) viewHolder.itemView);
            }
        });

    }

    private void initEvent() {
        Subscription subscription1 = RxBus.getDefault()
                .toObservable(DownRecordInfo.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DownRecordInfo>() {
                    @Override
                    public void call(DownRecordInfo downRecordInfo) {
                        int state = downRecordInfo.getState();
                        if (state == DownloadManager.CANCEL_TASK) {
                            for (SoftwareInfo Info : mApps) {
                                String packageName = Info.getPackageName();
                                if (packageName.equals(downRecordInfo.getPackageName())) {
                                    int i = mApps.indexOf(Info);
                                    MyAppCardView myAppCardView = (MyAppCardView) mRecyclerView.getChildAt(i);
                                    if (myAppCardView != null) {
                                        AppDownloadPresenter presenter = myAppCardView.getPresenter();
                                        if (presenter != null) {
                                            presenter.destroy();
                                            mAdapter.downloadList.remove(presenter);
                                            myAppCardView.setPresenter(null);
                                        }
                                        if (ManagerUtil.checkApplication(mContext, packageName)) {
                                            Info.setInstalled(true);
                                            Info.setDownRecordInfo(null);
                                            mAdapter.notifyItemChanged(i);
                                            if (isShow) {
                                                mAdapter.getBlowUpUtil().setScaleDown();
                                                mAdapter.setRequestFocusPosition(i);
                                            }
                                            downloadApps.add(Info);
                                            return;
                                        }

                                    }
                                    mApps.remove(Info);
                                    if (isShow) {
                                        mAdapter.notifyItemRemoved(i + 1);
                                        mAdapter.getBlowUpUtil().setScaleDown();
                                        mAdapter.setRequestFocusPosition(i);
                                        notifyDataSetChanged();
                                    } else {
                                        mAdapter.notifyItemRemoved(i + 1);
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    return;
                                }
                            }
                        } else {
                            for (SoftwareInfo Info : mApps) {
                                String packageName = Info.getPackageName();
                                if (packageName.equals(downRecordInfo.getPackageName())) {
                                    boolean isDownloadAPP = false;
                                    for (SoftwareInfo softwareInfo : downloadApps) {
                                        if (packageName.equals(softwareInfo.getPackageName())) {
                                            isDownloadAPP = true;
                                            downloadApps.remove(softwareInfo);
                                            removeDownloadApp(packageName);
                                            break;
                                        }
                                    }
                                    View view = mRecyclerView.getChildAt(mApps.indexOf(Info));
                                    if (!isDownloadAPP) {
                                        if (view != null) {
                                            AppDownloadPresenter presenter = ((MyAppCardView) view).getPresenter();
                                            if (presenter == null) {
                                                isDownloadAPP = true;
                                            }
                                        } else {
                                            isDownloadAPP = true;
                                        }
                                    }
                                    if (isDownloadAPP) {
                                        mApps.remove(Info);
                                        Info.setInstalled(false);
                                        Info.setDownRecordInfo(downRecordInfo);
                                        mApps.add(0, Info);
                                        mAdapter.setNoOpenApps();
                                        mAdapter.getBlowUpUtil().setScaleDown();
                                        mAdapter.notifyItemInserted(0);
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Info.setDownRecordInfo(downRecordInfo);
                                    }
                                    return;
                                }
                            }
                            SoftwareInfo softwareInfo = new SoftwareInfo();
                            softwareInfo.setName(downRecordInfo.getAppName());
                            softwareInfo.setPackageName(downRecordInfo.getPackageName());
                            softwareInfo.setInstalled(false);
                            softwareInfo.setVersionCode(downRecordInfo.getAppVersion());
                            softwareInfo.setIcon(downRecordInfo.getIconUrl());
                            softwareInfo.setIsWhite(true);
                            softwareInfo.setDownRecordInfo(downRecordInfo);
                            mApps.add(0, softwareInfo);
                            mAdapter.setNoOpenApps();
                            mAdapter.getBlowUpUtil().setScaleDown();
                            mAdapter.notifyItemInserted(0);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogDebugUtil.w("throwable", throwable.getMessage());
                    }
                });

        Subscription subscription2 = RxBus.getDefault()
                .toObservable(OpenAPkEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OpenAPkEvent>() {
                    @Override
                    public void call(OpenAPkEvent openAPkEvent) {
                        mAdapter.setNoOpenApps();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LoggerUtil.w(mContext.getResources().getString(R.string.app_open_error) + "throwable", throwable.getMessage());
                    }
                });
        compositeSubscription.add(subscription1);
        compositeSubscription.add(subscription2);

    }

    private void removeDownloadApp(String PackageName) {
        if (ContainerUtil.isEmpty(downloadApps)) {
            return;
        }
        for (SoftwareInfo softwareInfo : downloadApps) {
            String packageName = softwareInfo.getPackageName();
            if (packageName.equals(PackageName)) {
                downloadApps.remove(softwareInfo);
                break;
            }
        }
    }

    private void startUpdate(SoftwareInfo softwareInfo, int position, AppDownloadUi<ViewGroup> ui) {
        SoftwareInfo info = mApps.get(position);
        removeDownloadApp(info.getPackageName());
        mApps.remove(info);
        DownRecordInfo downRecordInfo = new DownRecordInfo();
        downRecordInfo.setAppName(info.getName());
        downRecordInfo.setPackageName(info.getPackageName());
        downRecordInfo.setState(DownloadManager.CREATE_TASK);
        info.setDownRecordInfo(downRecordInfo);
        info.setInstalled(false);
        mApps.add(0, info);
        mAdapter.getBlowUpUtil().setScaleDown();
        mAdapter.setRequestFocusPosition(position);
        notifyDataSetChanged();
    }

    private void checkUpdate() {
        if (ContainerUtil.isEmpty(mWhiteApps)) {
            return;
        }
        ModeLevelAmsInstall.checkUpdate(mContext, true, mWhiteApps, "0", null);
    }

    private ArrayList<SoftwareInfo> getDatas() {
        String packageList = Pref.getString(Pref.PACKAGE_LIST, mContext, "");
        HashMap<String, SoftwareInfo> userApps = AppManager.getAllApplicationInfo(getActivity());
        //添加下载App
        for (DownRecordInfo recordInfo : VpnStoreApplication.recordList) {
            SoftwareInfo softwareInfo;
            if (userApps.containsKey(recordInfo.getPackageName())) {
                if (recordInfo.getState() == DownloadManager.COMPLETE_DOWN_LOAD) {
                    DownloadManager.deleteAppRecordAndFile(mContext, recordInfo);
                } else {
                    softwareInfo = userApps.get(recordInfo.getPackageName());
                    userApps.remove(recordInfo.getPackageName());
                    softwareInfo.setNeedUpdate("1");
                    softwareInfo.setInstalled(false);
                    softwareInfo.setDownRecordInfo(recordInfo);
                    mApps.add(softwareInfo);
                }
            } else {
                softwareInfo = new SoftwareInfo();
                softwareInfo.setInstalled(false);
                softwareInfo.setName(recordInfo.getAppName());
                softwareInfo.setPackageName(recordInfo.getPackageName());
                softwareInfo.setVersionCode(recordInfo.getAppVersion());
                softwareInfo.setIsWhite(true);
                softwareInfo.setIcon(recordInfo.getIconUrl());
                softwareInfo.setDownRecordInfo(recordInfo);
                mApps.add(softwareInfo);
            }
        }
        if (TextUtils.isEmpty(packageList)) {
            mApps.addAll(userApps.values());
        } else {
            String updateStrs = PrefNormalUtils.getString(mContext, PrefNormalUtils.UPDATE_LIST, null);
            PackageList packageList1 = new Gson().fromJson(packageList, PackageList.class);
            List<String> onlinePackages = packageList1.getOnlinePackages();
            List<String> packageNames = new ArrayList<>(userApps.keySet());
            ArrayList<SoftwareInfo> updateList = null;
            if (!TextUtils.isEmpty(updateStrs)) {
                updateList = new Gson().fromJson(updateStrs, new TypeToken<ArrayList<SoftwareInfo>>() {
                }.getType());
            }
            for (int i = 0; i < packageNames.size(); i++) {
                String packageName = packageNames.get(i);
                SoftwareInfo soft = userApps.get(packageName);
                if (onlinePackages.contains(packageName)) {
                    soft.setWhite(true);
                    if (!ContainerUtil.isEmpty(updateList)) {
                        for (SoftwareInfo softwareInfo : updateList) {
                            if (softwareInfo.getPackageName().equals(packageName)) {
                                int versionCode = Integer.valueOf(softwareInfo.getVersionCode());
                                int localVersionCode = Integer.valueOf(userApps.get(packageName).getVersionCode());
                                if (versionCode > localVersionCode) {
                                    soft.setNeedUpdate("1");
                                    downloadApps.add(soft);
                                }
                            }
                        }
                    }
                    mWhiteApps.add(soft);
                } else {
                    soft.setWhite(false);
                    mOtherApps.add(soft);
                }
            }
            mApps.addAll(mWhiteApps);
            checkUpdate();
            if (show_other_app == 1) {
                mApps.addAll(mOtherApps);
            }

        }
        Collections.sort(mApps, mTimeComparator);
        return mApps;
    }

    BroadcastReceiver myUpdateMyApp = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString().substring(8);
                LogDebugUtil.d("myApp", "android.intent.action.PACKAGE_ADDED " + packageName);
                boolean isAdd = true;
                for (SoftwareInfo softwareInfo : mApps) {
                    if (softwareInfo.getPackageName().equals(packageName)) {
                        softwareInfo.setInstalled(true);
                        softwareInfo.setNeedUpdate("");
                        MyAppCardView myAppCardView = (MyAppCardView) mRecyclerView.getChildAt(mApps.indexOf(softwareInfo));
                        if (myAppCardView != null && myAppCardView.getPresenter() != null) {
                            mAdapter.downloadList.remove(myAppCardView.getPresenter());
                        }
                        softwareInfo.setDownRecordInfo(null);
                        isAdd = false;
                        break;
                    }
                }
                if (isAdd) {
                    SoftwareInfo softwareInfo = AppManager.getAppMessage(mContext, packageName);
                    if (softwareInfo != null) {
                        mApps.add(softwareInfo);
                        mAdapter.notifyItemInserted(mApps.size() - 1);
                    }
                }

            } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                //应用更新先remove再add
                String packageName = intent.getDataString().substring(8);
                LogDebugUtil.d("myApp", "android.intent.action.PACKAGE_REMOVED " + packageName);

                int size = mApps.size();
                for (int i = 0; i < size; i++) {
                    if (mApps.get(i).getPackageName().equals(packageName)) {
                        if (!mApps.get(i).isInstalled()) {
                            return;
                        }
                        ManagerUtil.deleteOpenApp(mContext, packageName);
                        removeDownloadApp(packageName);
                        MainActivity.isShowMenu = false;
                        mAdapter.getBlowUpUtil().setScaleDown();
                        mApps.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        View currentFocus = getActivity().getCurrentFocus();
                        boolean isChild = false;
                        if (currentFocus != null) {
                            for (ViewParent parent = currentFocus.getParent(); parent instanceof ViewGroup;
                                 parent = parent.getParent()) {
                                if (parent == mRecyclerView) {
                                    isChild = true;
                                    break;
                                }
                            }
                        }

                        if (isChild) {
                            if (i == size - 1) {
                                mAdapter.getBlowUpUtil().setScaleDown();
                                mAdapter.setRequestFocusPosition(i - 1);
                                notifyDataSetChanged();

                            } else {
                                mAdapter.setRequestFocusPosition(i);
                                mAdapter.getBlowUpUtil().setScaleDown();
                                notifyDataSetChanged();
                            }
                        } else {
                            mAdapter.getBlowUpUtil().setScaleDown();
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                }
            }
//            else if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
//                String packageName = intent.getDataString().substring(8);
//                LogDebugUtil.d("myApp", "android.intent.action.PACKAGE_REPLACED" + packageName);
//
//            }
        }
    };

    // 根据使用和安装时间来排序

    Comparator<SoftwareInfo> mTimeComparator = new Comparator<SoftwareInfo>() {

        @Override
        public int compare(SoftwareInfo lhs, SoftwareInfo rhs) {
            if (lhs.getInstallTime() > rhs.getInstallTime()) {
                return 1;
            }
            if (lhs.getInstallTime() < rhs.getInstallTime()) {
                return -1;
            }
            return 0;
        }

    };

    @Override
    public void focusMoveToLeft() {
        LogDebugUtil.e("focusMoveToLeft", "focusMoveToLeft");
    }

    @Override
    public void focusMoveToRight() {

        int childCount = mRecyclerView.getChildCount();
        int row = (int) (Math.ceil((double) childCount / mLayoutManager.getSpanCount()));
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        int cvPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int vPosition = mLayoutManager.findLastVisibleItemPosition();
        int effect = (cvPosition == vPosition ? 0 : 1);
        row = row - effect;
        int focusPosition;
        if (row <= 1) {
            focusPosition = childCount <= 6 ? childCount : 6;
        } else {
            int effect2 = (firstPosition == 0 ? 1 : 2);
            focusPosition = effect2 * mLayoutManager.getSpanCount();
        }
        mRecyclerView.getChildAt(focusPosition - 1).requestFocus();
    }

    @Override
    public void focusTab2Down() {
        if (mRecyclerView != null) {
            new Thread() {
                public void run() {
                    try {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
                    } catch (Exception e) {
                        LogDebugUtil.e("sendPointerSync", e.toString());
                    }
                }
            }.start();
        }
    }


    @Override
    public boolean onKeyBottomDown() {
        return true;
    }

    @Override
    public boolean onKeyTopUp() {
        return true;
    }

    @Override
    public boolean onKeyLeftEnd() {
        if (mLeftMenuTv.getVisibility() == View.VISIBLE) {
            PrefNormalUtils.putBoolean("leftMenuOpen", true);
            mLeftMenuTv.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    @Override
    public boolean onKeyRightEnd() {
        return false;
    }

    /**
     * 从侧边栏右切返回到我的应用
     */
    private void itemRequestFocus() {
        int childCount = mRecyclerView.getChildCount();
        int row = (int) (Math.ceil((double) childCount / mLayoutManager.getSpanCount()));
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        int cvPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int vPosition = mLayoutManager.findLastVisibleItemPosition();
        int effect = (cvPosition == vPosition ? 0 : 1);
        row = row - effect;
        int focusPosition;
        int effect2 = firstPosition == 0 ? 1 : 2;
        focusPosition = effect2 * mLayoutManager.getSpanCount();
        if (firstPosition == 0) {
            mRecyclerView.getChildAt(0).requestFocus();
        } else {
            mRecyclerView.getChildAt(focusPosition - 6).requestFocus();
        }
    }

    private void updateAllDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(mContext).create();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_update_all, null);
        if (view != null) {
            TextView updateAll = (TextView) view.findViewById(R.id.tv_dialog_update_all_confirm);
            TextView updateCancel = (TextView) view.findViewById(R.id.tv_dialog_update_all_cancel);
            updateAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAll();
                    alertDialog.dismiss();
                }
            });
            updateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            Window window = alertDialog.getWindow();
            assert window != null;
            window.setBackgroundDrawableResource(R.color.transparent);
            alertDialog.show();
            alertDialog.setContentView(view);
            window.setLayout(ScreenUtil.dip2px(mContext, 350), ScreenUtil.dip2px(mContext, 200));
        }
    }

    private void updateAll() {
        ArrayList<SoftwareInfo> copyMApps = new ArrayList<>();
        copyMApps.addAll(mApps);
        for (SoftwareInfo info : copyMApps) {
            String packageName = info.getPackageName();
            for (SoftwareInfo softwareInfo : downloadApps) {
                if (softwareInfo.getPackageName().equals(packageName)) {
                    mApps.remove(copyMApps.indexOf(info));
                    DownRecordInfo downRecordInfo = new DownRecordInfo();
                    downRecordInfo.setAppName(info.getName());
                    downRecordInfo.setPackageName(info.getPackageName());
                    downRecordInfo.setState(DownloadManager.CREATE_TASK);
                    info.setDownRecordInfo(downRecordInfo);
                    info.setInstalled(false);
                    mApps.add(0, info);
                }
            }
        }
        downloadApps.clear();
        mAdapter.notifyDataSetChanged();
    }
}
