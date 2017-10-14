package com.winhearts.arappmarket.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * Description: 修改字体
 * Created by lmh on 2016/3/21.
 */
public class FontManager {
    private static String TAG = "FontManager";

    public static Typeface getMSYHTypefaceFromAssets() {
        return FontTypeFaceMgr.getInstance().getMSYHTypefaceFromAssets();
    }


    /**
     * 批量修改viewGroup 内字体
     *
     * @param root
     */
    public static void changeViewGroupFonts(ViewGroup root) {

        Typeface tf = getMSYHTypefaceFromAssets();
        if (tf == null) {
            LogDebugUtil.e(TAG, "MSYH_Typeface == null");
            return;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeViewGroupFonts((ViewGroup) v);
            }
        }

    }

    public static final void setViewGroupFont(ViewGroup container) {
        setViewGroupFont(container, getMSYHTypefaceFromAssets(), true);
    }

    /**
     * 批量修改ViewGroup 内字体
     *
     * @param container
     * @param font
     * @param reflect
     */
    public static final void setViewGroupFont(ViewGroup container, Typeface font, boolean reflect) {
        if (container == null || font == null) {
            return;
        }

        int count = container.getChildCount();

        // Loop through all of the children.
        for (int i = 0; i < count; ++i) {
            View childView = container.getChildAt(i);
            if (childView instanceof TextView) {
                // Set the font if it is a TextView.
                ((TextView) childView).setTypeface(font);
            } else if (childView instanceof ViewGroup) {
                // Recursively attempt another ViewGroup.
                setViewGroupFont((ViewGroup) childView, font, reflect);
            } else if (reflect) {
                try {
                    Method setTypeface = childView.getClass().getMethod("setTypeface", Typeface.class);
                    setTypeface.invoke(childView, font);
                } catch (Exception e) { /* Do something... */ }
            }
        }
    }

    public static final void setViewFont(View v, boolean reflect) {
        Typeface font = getMSYHTypefaceFromAssets();

        if (v == null || font == null) {
            LogDebugUtil.w(TAG, "v == null || font == null");
            return;
        }

        // Loop through all of the children.
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(font);
        } else if (v instanceof Button) {
            ((Button) v).setTypeface(font);
        } else if (v instanceof EditText) {
            ((EditText) v).setTypeface(font);
        } else if (v instanceof ViewGroup) {
            // Recursively attempt another ViewGroup.
            setViewGroupFont((ViewGroup) v, font, reflect);
        } else if (reflect) {
            try {
                Method setTypeface = v.getClass().getMethod("setTypeface", Typeface.class);
                setTypeface.invoke(v, font);
            } catch (Exception e) {
                LogDebugUtil.w(TAG, "e: " + Log.getStackTraceString(e));
            }
        }

    }

    public static View getRootView(Activity context)
    {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
    }

    static public void setActivityFont(Activity context){
        View rootView = getRootView(context);
        if (rootView != null){
            setViewFont(rootView, false);
        }
    }
}  
