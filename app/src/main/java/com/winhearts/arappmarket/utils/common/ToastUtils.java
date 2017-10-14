package com.winhearts.arappmarket.utils.common;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * ToastUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-9
 */
public class ToastUtils {

    private static Toast mToast;
    private static TextView tv;

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_LONG);
    }


    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_LONG);
    }

    public static void show(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = new Toast(VpnStoreApplication.app);
            LayoutInflater inflate = (LayoutInflater)
                    VpnStoreApplication.app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tv = (TextView) inflate.inflate(R.layout.view_toast, null);
            tv.setText(text);
            mToast.setView(tv);
            mToast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.dip2px(VpnStoreApplication.app, 54));
            mToast.setDuration(duration);
        } else {
            tv.setText(text);
        }
        mToast.show();
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_LONG);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_LONG);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void show(Context context, int code, Throwable e) {
        switch (code) {
            case SubVolleyResponseHandler.REQUEST_FAIL:
                show(context, context.getResources().getString(R.string.connection_fail));
                break;
            default:
                String value = e.getMessage().contains("refused") ? context.getResources().getString(R.string.connection_fail) : e.getMessage();
                show(context, value);
                break;
        }
    }
}
