package com.winhearts.arappmarket.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.download.loader.UpdateAsyncTaskUtil;
import com.winhearts.arappmarket.model.AppUpdate;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.VersionNo;
import com.winhearts.arappmarket.modellevel.ModeLevelAms;
import com.winhearts.arappmarket.modellevel.ModeLevelFile;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.UpdateHintDialog;

import java.lang.ref.WeakReference;

/**
 * 应用初始化逻辑
 * Created by lmh on 2016/11/21.
 */

public class InitLogic {
    private static final String TAG = "InitLogic";
    private Context mContext;
    private boolean focusUpdate;
    private UpdateHintDialog dialog;
    private static final int LOCAL_LAYOUT_NULL = 111;
    private static final int NET_LAYOUT_NULL = 112;

    public InitLogic(Context context) {
        mContext = context;
    }

    public void init() {

//        appUpdate();
//        queryConfig();
//        getRecommendAppData();
    }

    private void appUpdate() {
        ModeLevelVms.appUpdate(mContext, new ModeUser<AppUpdate>() {
            @Override
            public void onJsonSuccess(AppUpdate appUpdate) {
                if (appUpdate != null) {
                    focusUpdate = "1".equals(appUpdate.getUpdateType());
                    if ("1".equals(appUpdate.getResult())) {
                        askUpdate(mContext, appUpdate);
                    }
                }
            }

            @Override
            public void onRequestFail(Throwable e) {
                LoggerUtil.d(TAG, "Throwable" + e.toString());
            }
        });
    }

    private void getRecommendAppData() {
        if (Pref.getString(Pref.SHOW_RECOMMEND, mContext, "1").endsWith("1")) {
            String recentBrowse = PrefNormalUtils.getString(mContext, PrefNormalUtils.RECENT_BROWSE_APP, "");
            ModeLevelAms.getRecommendAppData(mContext, recentBrowse, new ModeUserErrorCode<Softwares>() {
                @Override
                public void onJsonSuccess(Softwares softwares) {
                    PrefNormalUtils.putString(mContext, PrefNormalUtils.RECOMMEND_APP, new Gson().toJson(softwares));
                }

                @Override
                public void onRequestFail(int code, Throwable e) {

                }
            });
        }
    }

    private void queryConfig() {
        ModeLevelVms.queryConfigForce(mContext, 2, TAG, new ModeUserErrorCode<ConfigInfo>() {
            @Override
            public void onJsonSuccess(ConfigInfo configInfo) {
                if (configInfo != null) {
                    ConfigInfo.savaConfig(mContext, configInfo);
                } else {
                    LogDebugUtil.d(TAG, "queryConfig onJsonSuccess: null");
                }
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                LogDebugUtil.d(TAG, "queryConfig onRequestFail: " + code);
            }
        });
    }

    private void askUpdate(Context context, final AppUpdate appUpdate) {

        if (dialog == null) {
            dialog = new UpdateHintDialog(context, UpdateHintDialog.Type_Can_Update, appUpdate.getVersionDesc(),
                    focusUpdate);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialog.setOnKeyListener(onKeyListener);
        }
        //按返回键是否能够退出，默认为true
//		dialog.setCancelable(false);


        dialog.setCancelButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.setConfirmButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String filepathString = appUpdate.getUpdateUrl();
                update(mContext, filepathString, Util.APK_NAME);
            }
        });
        dialog.setUpdateButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String filepathString = appUpdate.getUpdateUrl();

                update(mContext, filepathString, Util.APK_NAME);
            }

        });
        dialog.show();

    }

    //给dialog赋予按键监听
    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && dialog != null) {
                dialog.dismiss();
            }
            return false;
        }
    };

    public void update(final Context mContext, final String url, String apkName) {
        final UpdateAsyncTaskUtil updateAsyncTaskUtil = new UpdateAsyncTaskUtil(mContext, !focusUpdate);
        updateAsyncTaskUtil.setHandler(myHandler);
        updateAsyncTaskUtil.execute(url, apkName);
    }

    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InitLogic> mInitlogic;

        MyHandler(InitLogic initLogic) {
            super(Looper.getMainLooper());
            mInitlogic = new WeakReference<>(initLogic);
        }

        @Override
        public void handleMessage(Message msg) {
            InitLogic initLogic = mInitlogic.get();
            if (initLogic != null) {
                switch (msg.what) {
                    case Constant.DOWNLOAD_SUCCESS:
                        break;
                    case Constant.DOWNLOAD_FAIL:
                        ToastUtils.show(initLogic.mContext, "安装包下载失败", Toast.LENGTH_LONG);
                        break;
                    case Constant.CANCEL_DOWNLOAD:
                        break;
                    case Constant.DOWNLOAD_BACKGROUND:
                        break;
                }
            }
        }
    }

    public static void queryLayout(final Context context, final String layoutCode, final ModeUserErrorCode<Layout> userErrorCode) {

        VersionNo versionNo = new VersionNo();
        String terminalCode = layoutCode;
        String versionCode;
        String saveTerminalCode = Pref.getString(Pref.LAYOUT_CODE, context, "");
        String saveTerminalCodeOld = PrefNormalUtils.getString(context, PrefNormalUtils.LAYOUT_CODE_OLD, "");
        if (!TextUtils.isEmpty(saveTerminalCode)) {
            PrefNormalUtils.putString(context, PrefNormalUtils.LAYOUT_CODE_OLD, saveTerminalCode);
            terminalCode = saveTerminalCode;
        }
        //解决不同布局码和布局版本之间的问题
        if (!TextUtils.isEmpty(saveTerminalCodeOld) && !saveTerminalCode.equals(saveTerminalCodeOld)) {
            Pref.saveString(Pref.LAYOUT_VERSION, "", context);
        }
        versionCode = Pref.getString(Pref.LAYOUT_VERSION, context, "");

        versionNo.setVersionNo(versionCode);
        versionNo.setTerminalCode(terminalCode);
        ModeLevelAms.queryLayout(context, TAG, versionNo, new ModeUserErrorCode<Layout>() {
            @Override
            public void onJsonSuccess(Layout layout) {
                if (layout != null) {
                    ModeLevelFile.saveLoadImageFile(layoutCode, layout.getStartBkgImg());
                    ModeLevelFile.saveBgImageFile(layoutCode, layout.getBg());
                    Util.clearEmptyAndSortScreen(layout);

                    Pref.saveString(Pref.LAYOUT_VERSION, layout.getCurrentVerNo(), context);
                    Pref.saveString(Pref.LAYOUT_STRING, new Gson().toJson(layout), context);
                    LayoutInfoSubject.onSuccess(layout);
                } else {
                    LayoutInfoSubject.onFail(NET_LAYOUT_NULL, "queryLayout==null");
                    LoggerUtil.i("queryLayout", "null");
                }

            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                LayoutInfoSubject.onFail(code, e.getMessage());
                if (code != 2) {
                    LoggerUtil.i("queryLayout", "onRequestFail" + code + e.getMessage());
                }
            }
        });


    }

    private static void strFromJson(String layoutStr, ModeUserErrorCode<Layout> userErrorCode) {
        if (userErrorCode != null) {
            if (TextUtils.isEmpty(layoutStr)) {
                userErrorCode.onRequestFail(LOCAL_LAYOUT_NULL, new Throwable("LOCAL_LAYOUT = NULL"));
            } else {
                Layout saveLayout = new Gson().fromJson(layoutStr, Layout.class);
                userErrorCode.onJsonSuccess(saveLayout);
            }
        }
    }
}
