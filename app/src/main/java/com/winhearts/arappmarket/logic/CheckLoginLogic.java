package com.winhearts.arappmarket.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.winhearts.arappmarket.event.LoginEvent;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.AccountIDToken;
import com.winhearts.arappmarket.model.ConfigInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsNewReply;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.activity.AccountLoginActivity;
import com.winhearts.arappmarket.activity.VpnStoreApplication;

import java.util.Map;
import java.util.Random;

/**
 * 登录检查逻辑
 * Created by lmh on 2016/4/22.
 */
public class CheckLoginLogic {
    private static String TAG = "CheckLoginLogic";
    private Context context;

    private static CheckLoginLogic checkLoginLogic = null;

    private CheckLoginLogic() {
        context = VpnStoreApplication.getApp();
    }

    public static CheckLoginLogic getInstance() {
        CheckLoginLogic loginLogic = checkLoginLogic;
        if (loginLogic == null) {
            synchronized (CheckLoginLogic.class) {
                loginLogic = checkLoginLogic;
                if (loginLogic == null) {
                    loginLogic = new CheckLoginLogic();
                    checkLoginLogic = loginLogic;
                }
            }
        }
        return loginLogic;
    }

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            checkLoginState(context, false, Constant.FROM_NORMAL);
        }
    };

    public void checkLogin(boolean isDelay) {
        if (isDelay) {
            String timeString = Pref.getString(Pref.HASH_TIME, context, "10800").trim();
            if (TextUtils.isEmpty(timeString)) {
                timeString = "10800";
            }
            int timeInterval = Integer.parseInt(timeString);
            if (timeInterval == 0) {
                handler.sendEmptyMessageDelayed(0, 30000);
                return;
            }
            Random random = new Random();
            int delayMillis = random.nextInt(timeInterval);
            handler.sendEmptyMessageDelayed(0, delayMillis * 1000);
        } else {
            if (handler.hasMessages(0)) {
                handler.removeMessages(0);
            }
            handler.sendEmptyMessage(0);
        }

    }

    /**
     * 检测登录状态
     *
     * @param context
     * @param isJumpLoginGui 是否弹出界面
     * @param direct         1 = pay获取uid的检测 2 = pay 获取订单的检测 0 = 默认不处理
     */
    public void checkLoginState(final Context context, final boolean isJumpLoginGui, final int direct) {
        // 避免第一次使用不能默认登录，先查看下vms对应状态
        String s = Pref.getString(Pref.AMS_URL, context, null);
        if (TextUtils.isEmpty(s)) {
            if (isJumpLoginGui) {
                ModeLevelVms.queryConfig(context, new ModeUser<ConfigInfo>() {

                    @Override
                    public void onRequestFail(Throwable e) {

                    }

                    @Override
                    public void onJsonSuccess(ConfigInfo t) {
                        // TODO Auto-generated method stub
                        if (t != null) {
                            ConfigInfo.savaConfig(context, t);
                            loginCheck(context, true, direct);

                        }
                    }
                });
            }
        } else {
            loginCheck(context, isJumpLoginGui, direct);
        }

    }

    /**
     * 根据是否有登录信息，进行登录或检测登录状态
     */
    public void loginCheck(final Context context, final boolean isJumpLoginGui, final int direct) {
        ConstInfo.update(context);
        if (TextUtils.isEmpty(ConstInfo.accountTokenId)) {
            ModeLevelAccount.registerByThirds(context, new ModeUserErrorCode<AccountIDToken>() {
                @Override
                public void onJsonSuccess(AccountIDToken accountIDToken) {
                    if (accountIDToken != null) {
                        ConstInfo.setAccountId2Pref(context, accountIDToken.getWsId(), accountIDToken.getLoginToken());
                        RxBus.getDefault().post(
                                new LoginEvent(ConstInfo.accountWsId, ConstInfo.accountTokenId, direct));
                        ModeLevelAccount.getUserInfo(context, accountIDToken.getWsId(), accountIDToken.getLoginToken(), null);
                    }
                }

                @Override
                public void onRequestFail(int code, Throwable e) {
                    if (isJumpLoginGui) {
                        Intent intent = new Intent(context, AccountLoginActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, direct);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });
        } else {
            ModeLevelAccount.checkLoginStatue(context, direct, ConstInfo.accountTokenId,
                    new ModeUser<Map<String, String>>() {

                        @Override
                        public void onJsonSuccess(Map<String, String> map) {
                            if (map == null) {
                                return;
                            }
                            String status = map.get("status");
                            if ("1".equals(status)) {// 已经登录
                                ModeLevelAmsNewReply.getNewReplyList(context, context.getClass().getName());
                                RxBus.getDefault().post(
                                        new LoginEvent(ConstInfo.accountWsId, ConstInfo.accountTokenId, direct));
                            } else if ("0".equals(status)) {// 未登录
                                ConstInfo.setAccountId2Pref(context, null, null);
                                ConstInfo.setAccountInfo2Pref(context, null);
                                if (isJumpLoginGui) {
                                    Intent intent = new Intent(context, AccountLoginActivity.class);
                                    intent.putExtra(Constant.FROM_TYPE, direct);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } else {
                                    loginCheck(context, false, direct);
                                }

                            }

                        }

                        @Override
                        public void onRequestFail(Throwable e) {
                            ConstInfo.setAccountId2Pref(context, null, null);
                            ConstInfo.setAccountInfo2Pref(context, null);
                            if (isJumpLoginGui) {
                                Intent intent = new Intent(context, AccountLoginActivity.class);
                                intent.putExtra(Constant.FROM_TYPE, direct);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });
        }

    }
}
