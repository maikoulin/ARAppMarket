package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.logic.InitLogic;
import com.winhearts.arappmarket.logic.LayoutInfoSubject;
import com.winhearts.arappmarket.logic.PollingLogReportLogic;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.service.InstallHintService;
import com.winhearts.arappmarket.utils.ActivityStack;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.RecordAppWatchUtil;
import com.winhearts.arappmarket.utils.ViewUtils;
import com.winhearts.arappmarket.utils.common.PackageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.LayoutInfoObserver;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.ExitHintDialog;
import com.winhearts.arappmarket.view.NewUserDialog;
import com.winhearts.arappmarket.view.NewUserExitDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity{
    private final static String TAG = "MainActivity";
    private boolean isDebug = false;
    public static String oldMenuId = null;
    public static String oldSubMenuId = null;
    public static boolean isShowMenu = false;
    private boolean mLayoutSuccess = false;
    private Context mContext;
    private long mExitTime = 0;
    private List<MenuItem> menuItems;
    private ExitHintDialog exitHintDialog;
    private NewUserExitDialog newUserExitDialog;
    private LayoutInfoObserver mLayoutInfoObserver;
    private RecordAppWatchUtil mRecordAppWatchUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        Constant.INSTALL_SHOW = false;
        setContentView(R.layout.main_activity);
        setLoadAndErrorView(R.id.view_load_and_error);
        mContext = this;
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        getNewUserRecommend();
        initLayout();
        setUpdateLayout();
        Intent installService = new Intent(mContext, InstallHintService.class);
        mContext.startService(installService);

        if (mRecordAppWatchUtil == null) {
            mRecordAppWatchUtil = new RecordAppWatchUtil(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterLayoutObserver();
        VpnStoreApplication.getApp().setLayoutCode(null);
        PollingLogReportLogic.logReport();
        InitLogic.mIsRequestSuccess = false;
        ActivityStack.getActivityStack().clearOtherActivity(this);
        super.onDestroy();
        if (exitHintDialog != null) {
            exitHintDialog.dismiss();
        }
        if (newUserExitDialog != null) {
            newUserExitDialog.dismiss();
        }
        stopService(new Intent(MainActivity.this, InstallHintService.class));
        MainActivity.oldMenuId = null;
        MainActivity.oldSubMenuId = null;
        VolleyQueueController.getInstance().cancelAll(TAG);
        ViewUtils.unbindDrawables(findViewById(R.id.main_tabs_container));
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
//        imagePipeline.clearMemoryCaches();
//        //清空硬盘缓存，一般在设置界面供用户手动清理
//        imagePipeline.clearDiskCaches();
//        //同时清理内存缓存和硬盘缓存
//        imagePipeline.clearCaches();
//        System.gc();

    }


    private void unregisterLayoutObserver() {
        if (mLayoutInfoObserver == null) {
            return;
        }
        LayoutInfoSubject.unregisterObserve(mLayoutInfoObserver);
    }

    @Override
    protected void onReTryClicked() {
        initLayout();
    }

    private void setUpdateLayout() {
        //更新布局
        mLayoutInfoObserver = new LayoutInfoObserver() {
            @Override
            public void onSuccess(final Layout layout) {
                mLayoutSuccess = true;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initFirstLevelMenu(layout);
                    }
                });

            }

            @Override
            public void onFail(final int code, String description) {
                if (!mLayoutSuccess) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showError(code);
                        }
                    });
                }

            }
        };
        LayoutInfoSubject.registerObserve(mLayoutInfoObserver);
    }

    private void initLayout() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new InitLogic(getApplicationContext()).init();
            }
        }).start();
        queryLayout(mContext);

    }

    private Handler mHandler = new Handler();

    private void queryLayout(Context context) {
        showLoading();
        String layoutCode = VpnStoreApplication.getApp().getLayoutCode();
        InitLogic.queryLayout(context, layoutCode, new ModeUserErrorCode<Layout>() {
            @Override
            public void onJsonSuccess(final Layout layout) {
                if (layout != null) {
                    mLayoutSuccess = true;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT < 19) {
                                //延迟加载，让背景先出来，而不是黑屏
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initFirstLevelMenu(layout);
                                    }
                                }, 500);
                            } else {
                                initFirstLevelMenu(layout);

                            }
                        }
                    });
                } else {
                    showOtherError("屏幕获取失败,null");
                }
            }

            @Override
            public void onRequestFail(final int code, Throwable e) {
                LogDebugUtil.i("onReQuestFail", code + e.getMessage());
            }
        });
    }

    //新用户推荐
    private void getNewUserRecommend() {
        if (PrefNormalUtils.getString(MainActivity.this, PrefNormalUtils.IS_NEW_USER, "1").equals("1")) {
            PrefNormalUtils.putString(MainActivity.this, PrefNormalUtils.IS_NEW_USER, "0");
            String topicCode = Pref.getString(Pref.NEW_USER_RECOMMEND, this, "NEW");
            if (topicCode.equals("")) {
                return;
            }
            Topic topic = new Topic();
            topic.setCode(topicCode);
            ModeLevelAmsMenu.queryTopocSoftWareList(this, TAG, 1, 10, topic,
                    new ModeUserErrorCode<Softwares>() {

                        @Override
                        public void onRequestFail(int errorCode, Throwable e) {
                            LogDebugUtil.d(TAG, "onRequestFail: " + e.getMessage());
                        }

                        @Override
                        public void onJsonSuccess(Softwares t) {
                            List<SoftwareInfo> softwareInfos = filterInstalledApp(t.getSoftwares());
                            if (softwareInfos.size() > 0) {
                                new NewUserDialog(MainActivity.this, softwareInfos).show();
                            }
                        }
                    });
        }

    }

    private List<SoftwareInfo> filterInstalledApp(List<SoftwareInfo> softwareInfos) {
        List<SoftwareInfo> softwareInfoNew = new ArrayList<>();
        for (int i = 0; i < softwareInfos.size(); i++) {
            boolean isInstall = PackageUtils.isAppInstalled(mContext, softwareInfos.get(i).getPackageName());
            if (!isInstall) {
                softwareInfoNew.add(softwareInfos.get(i));
            }
        }
        if (softwareInfoNew.size() > 8) {
            return softwareInfoNew.subList(0, 8);
        }
        return softwareInfoNew;
    }

    private void initFirstLevelMenu(Layout layout) {
        hideLoading();
        menuItems = layout.getMenuInfos();
        String layoutId = layout.getLayoutId();
        PrefNormalUtils.putString(mContext, PrefNormalUtils.LAYOUT_ID, layoutId);
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem menuItem = menuItems.get(i);
            String name = menuItem.getName();
            Bundle args = new Bundle();
            args.putInt("index", i);
            args.putInt("tab_count", menuItems.size());
            args.putString("typeName", name);
            args.putString("layoutId", layoutId);
            if (menuItem.getHasChild() == 1) {
                MenuItem chileMenuItem = menuItem.getChild().get(0);
                args.putSerializable("object", menuItem.getChild());
                menuItem.setMenuDataType(chileMenuItem.getMenuDataType());
                menuItem.setScreens(chileMenuItem.getScreens());
            } else {
                args.putSerializable("object", menuItem);
            }

            if ("SELF_DEFINE".equals(menuItem.getMenuDataType())) {

            } else {

            }
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0 && !isShowMenu) {

            if (PrefNormalUtils.getString(PrefNormalUtils.IS_NEW_USER_exit, "1").equals("1")) {
                if (newUserExitDialog == null) {
                    newUserExitDialog = new NewUserExitDialog(this);
                }
                newUserExitDialog.show();
                return true;
            }
            if (Pref.getString(Pref.IS_EXIT_DIALOG_SHOW, this, "0").equals("1")) {
                if (exitHintDialog == null) {
                    exitHintDialog = new ExitHintDialog(this);
                }
                exitHintDialog.show();
            } else if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.show(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        return super.dispatchKeyEvent(event);
    }
}
