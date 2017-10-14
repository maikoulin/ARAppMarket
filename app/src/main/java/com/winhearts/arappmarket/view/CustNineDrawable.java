package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.winhearts.arappmarket.R;


/**
 * Description：用作卡片的背景，保持框的大小，不被card大小限制
 * 主要用于item放大时使用
 * Created by lmh on 2016/1/13.
 */
public class CustNineDrawable extends Drawable {
    private Drawable mScaleFocusDrawable = null;

    public CustNineDrawable(Context context) {
        mScaleFocusDrawable = context.getResources().getDrawable(R.drawable.item_shadow_v);
    }

    public void setFocusDrawable(Context context, int drawableId) {
        mScaleFocusDrawable = context.getResources().getDrawable(drawableId);
    }

    @Override
    public void draw(Canvas canvas) {
        mScaleFocusDrawable.setBounds(getBounds());
        mScaleFocusDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        Rect padding = new Rect();
        mScaleFocusDrawable.getPadding(padding);
        int l = (left - padding.left);
        int t = (top - padding.top);
        int r = (right + padding.right);
        int b = (bottom + padding.bottom);

        super.setBounds(l, t, r, b);
    }

    public int getSrcLeftPadding() {
        Rect padding = new Rect();
        mScaleFocusDrawable.getPadding(padding);
        return padding.left;
    }

    public int getSrcRightPadding() {
        Rect padding = new Rect();
        mScaleFocusDrawable.getPadding(padding);
        return padding.right;
    }
}
