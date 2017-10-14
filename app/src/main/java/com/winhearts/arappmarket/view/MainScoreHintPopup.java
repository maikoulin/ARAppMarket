package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * 主页签到送积分引导popup
 * Created by lmh on 2016/1/14.
 */
public class MainScoreHintPopup {

    private String TAG = "MainScoreHintPopup";
    private PopupWindow mPopupWindow;
    private View popupView;
    private Activity context;
    private TextView tvHint;


    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                if (context != null && !context.isFinishing()) {
                    mPopupWindow.dismiss();
                }

                LogDebugUtil.d(TAG, "view.postDelayed(new Runnable() {");
            }
        }
    };

    MainScoreHintPopup(Activity context) {
        this.context = context;
        popupView = LayoutInflater.from(context).inflate(R.layout.popwindow_score_hint, null);
        tvHint = (TextView) popupView.findViewById(R.id.tv_pop_hint_content);
        mPopupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                false);
//        mPopupWindow.setAnimationStyle(R.style.activity_slide_item);
    }

    public void show(View view, int x, int y, int resId, String hint) {
        tvHint.setText(hint);
        popupView.setBackgroundResource(resId);
        if (mPopupWindow != null) {
            int[] local = new int[2];
            view.getLocationInWindow(local);
            popupView.measure(0, 0);
            mPopupWindow.showAtLocation(view,
                    Gravity.NO_GRAVITY, local[0] - popupView.getMeasuredWidth() + ScreenUtil.dip2px(context, x),
                    local[1] + popupView.getMeasuredHeight() + ScreenUtil.dip2px(context, y));
            handler.postDelayed(runnable, 10000);
        }
    }

    public void destroy() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
        handler.removeCallbacks(runnable);
    }
}
