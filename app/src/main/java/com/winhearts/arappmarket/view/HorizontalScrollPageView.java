package com.winhearts.arappmarket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 自定义横向滑动速度
 * Created by lmh on 2016/4/28.
 */
public class HorizontalScrollPageView extends FrameLayout {
    public Scroller mScroller;

    public HorizontalScrollPageView(Context context) {
        super(context);
        initScroller();
    }

    public HorizontalScrollPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScroller();
    }

    public HorizontalScrollPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScroller();
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();

        }
        super.computeScroll();
    }

    private void initScroller() {
        mScroller = new Scroller(getContext());
    }


    public void smoothScrollTo(int x, int y, int addDuration) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), x - mScroller.getFinalX(), mScroller.getFinalY(), addDuration);
        invalidate();
    }


    private interface ScrollerEx {

        void create(Context context, Interpolator interpolator);

        Object getScroller();

        void abortAnimation();

        void startScroll(int startX, int startY, int dx, int dy, int duration);

        boolean isFinished();
    }
}
