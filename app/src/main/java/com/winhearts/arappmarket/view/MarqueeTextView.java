package com.winhearts.arappmarket.view;

/**
 * TextView跑马灯
 * Created by UMUTech on 2015/10/5.
 */

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean marquee = false;

    public void setMarquee(boolean flag) {
        if (marquee != flag) {
            marquee = flag;
            if (flag) {
                setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                setEllipsize(TextUtils.TruncateAt.END);
            }
            onWindowFocusChanged(flag);
        }
    }

    public boolean isMarquee() {
        return marquee;
    }

    @Override
    public boolean isFocused() {
        return marquee;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(marquee, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(marquee);
    }
}
