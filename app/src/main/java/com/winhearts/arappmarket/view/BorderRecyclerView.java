package com.winhearts.arappmarket.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * 封装recycleView，用于我的用应
 * Created by lmh on 2016/8/15.
 */
public class BorderRecyclerView extends RecyclerView {
    private BorderListener borderListener;
    private double scale = 1;

    public BorderRecyclerView(Context context) {
        super(context);
    }

    public BorderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BorderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return isBorder(event);
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 判断焦点是否在边界
     *
     * @param event 当前焦点的下个事件
     * @return true or false
     */
    protected boolean isBorder(KeyEvent event) {
        if (borderListener == null) {
            return true;
        }
        int focusDirection = event.getKeyCode();
        View view = this.getFocusedChild();
        LayoutManager layoutManager = this.getLayoutManager();
        int focusPos = layoutManager.getPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int itemCount = layoutManager.getItemCount();
            int rowCount;
            int row;
            int span;

            rowCount = (int) Math.ceil((double) itemCount / spanCount);
            row = focusPos / spanCount + 1;
            span = focusPos % spanCount + 1;

            if (event.hasNoModifiers()) {
                switch (focusDirection) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (row == rowCount) {
                            return borderListener.onKeyBottomDown();
                        } else {
                            //处理长按焦点错误问题;
                            View nextView = view.focusSearch(View.FOCUS_DOWN);
                            if (nextView != null) {
                                if (!nextView.willNotDraw()) {
                                    return true;
                                }
                                if (nextView.getTop() > ScreenUtil.dip2px(getContext(), 526)) {
                                    return true;
                                }
                                boolean isChild = false;
                                for (ViewParent parent = nextView.getParent(); parent instanceof ViewGroup;
                                     parent = parent.getParent()) {
                                    if (parent == this) {
                                        isChild = true;
                                        break;
                                    }
                                }
                                return !isChild;
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (row == 1) {
                            return borderListener.onKeyTopUp();
                        } else {
                            //处理长按焦点错误问题
                            View nextView = view.focusSearch(View.FOCUS_UP);
                            if (nextView != null) {
                                if (!nextView.willNotDraw()) {
                                    return true;
                                }
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (span == spanCount) {
                            return borderListener.onKeyRightEnd();
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (span == 1) {
                            return borderListener.onKeyLeftEnd();
                        }
                        break;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void setflingScale(double scale) {
        this.scale = scale;
    }

    public void setBorderListener(BorderListener borderListener) {
        this.borderListener = borderListener;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= scale;
        return super.fling(velocityX, velocityY);
    }


    public interface BorderListener {
        boolean onKeyBottomDown();

        boolean onKeyTopUp();

        boolean onKeyLeftEnd();

        boolean onKeyRightEnd();
    }
}
