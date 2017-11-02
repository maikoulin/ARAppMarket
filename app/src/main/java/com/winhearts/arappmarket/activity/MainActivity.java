package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.logic.InitLogic;
import com.winhearts.arappmarket.logic.PollingLogReportLogic;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.service.InstallHintService;
import com.winhearts.arappmarket.utils.ActivityStack;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.ViewUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.ExitHintDialog;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.NewUserExitDialog;
import com.winhearts.arappmarket.view.RecommendCardView;

import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private boolean isDebug = false;
    public static String oldMenuId = null;
    public static String oldSubMenuId = null;
    public static boolean isShowMenu = false;
    private Context mContext;
    private long mExitTime = 0;
    private List<MenuItem> menuItems;
    private HorizontalLayout mMainLayout;
    private ExitHintDialog exitHintDialog;
    private NewUserExitDialog newUserExitDialog;
    private TextView tvPersonage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        Constant.INSTALL_SHOW = false;
        setContentView(R.layout.main_activity);
        setLoadAndErrorView(R.id.view_load_and_error);
        mMainLayout = (HorizontalLayout) this.findViewById(R.id.vl_main_content);
        tvPersonage = (TextView) this.findViewById(R.id.tv_main_personage);
        mContext = this;
        Intent installService = new Intent(mContext, InstallHintService.class);
        mContext.startService(installService);
        fillContent();
        String layoutString = Pref.getString(Pref.LAYOUT_STRING, mContext, "");
        if (!TextUtils.isEmpty(layoutString)) {
            Layout saveLayout = new Gson().fromJson(layoutString, Layout.class);
            initFirstLevelMenu(saveLayout);
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

    private void fillContent() {
        int with = ScreenUtil.dip2px(this, 310);
        int height = ScreenUtil.dip2px(this, 260);
        mMainLayout.setClipChildren(false);
        mMainLayout.setSize(with, height, 3, 2);
        RecommendCardView boutiqueView = new RecommendCardView(this)
                .bindData("res://drawable/" + R.drawable.ic_main_boutique_n, with, height / 3);
        mMainLayout.addItemView(boutiqueView, 0, 0, 0, 2, 1, 0, 6);
        RecommendCardView categoryView = new RecommendCardView(this)
                .bindData("res://drawable/" + R.drawable.ic_main_category_n, with, height / 3);
        mMainLayout.addItemView(categoryView, 0, 0, 1, 2, 2, 0, 6);
        RecommendCardView leftView = new RecommendCardView(this)
                .bindData("res://drawable/" + R.drawable.tv_main_other_menu, with / 2, height / 3);
        mMainLayout.addItemView(leftView, 0, 0, 2, 1, 3, 0, 6);
        RecommendCardView rightView = new RecommendCardView(this)
                .bindData("res://drawable/" + R.drawable.tv_main_other_menu, with / 2, height / 3);
        mMainLayout.addItemView(rightView, 0, 1, 2, 2, 3, 0, 6);
        boutiqueView.requestFocus();
        boutiqueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContainerUtil.isEmptyOrNull(menuItems)) {
                    queryLayout();
                } else {
                    Intent boutiqueIntent = new Intent(mContext, BoutiqueActivity.class);
                    MenuItem menuItem = menuItems.get(0);
                    Bundle args = new Bundle();
                    args.putSerializable("message", menuItem);
                    boutiqueIntent.putExtras(args);
                    startActivity(boutiqueIntent);
                }

            }
        });
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContainerUtil.isEmptyOrNull(menuItems)) {
                    queryLayout();
                } else {
                    if (menuItems.size() < 2) {
                        ToastUtils.show(mContext, "没有相应内容");
                    } else {
                        MenuItem menuItem = menuItems.get(1);
                        Intent boutiqueIntent = new Intent(mContext, CategoryActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("message", menuItem);
                        boutiqueIntent.putExtras(args);
                        startActivity(boutiqueIntent);
                    }
                }
            }

        });
        tvPersonage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        });
        mMainLayout.setOnBorderListener(new HorizontalLayout.OnBorderListener() {
            @Override
            public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
                return true;
            }

            @Override
            public boolean onKeyTopUp(int page, RectF rect) {
                return true;
            }

            @Override
            public boolean onKeyLeftEnd(int page) {
                tvPersonage.requestFocus();
                return true;
            }

            @Override
            public boolean onKeyRightEnd(int page) {
                return true;
            }
        });


    }


    @Override
    protected void onReTryClicked() {

    }


    private Handler mHandler = new Handler();

    private void queryLayout() {
        showLoading();
        String layoutCode = VpnStoreApplication.getApp().getLayoutCode();
        InitLogic.queryLayout(mContext, layoutCode, new ModeUserErrorCode<Layout>() {
            @Override
            public void onJsonSuccess(final Layout layout) {
                if (layout != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initFirstLevelMenu(layout);
                        }
                    });
                } else {
                    showOtherError("屏幕获取失败,null");
                }
            }

            @Override
            public void onRequestFail(final int code, Throwable e) {
                showOtherError("屏幕获取失败" + e.getMessage());
                LogDebugUtil.i("onReQuestFail", code + e.getMessage());
            }
        });
    }

    private void initFirstLevelMenu(Layout layout) {
        hideLoading();
        menuItems = layout.getMenuInfos();
        String layoutId = layout.getLayoutId();
        PrefNormalUtils.putString(mContext, PrefNormalUtils.LAYOUT_ID, layoutId);
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
