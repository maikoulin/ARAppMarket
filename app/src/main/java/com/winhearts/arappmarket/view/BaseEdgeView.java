package com.winhearts.arappmarket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.winhearts.arappmarket.R;


/**
 * 边界判断基类
 * Created by lmh on 2016/5/3.
 */
public class BaseEdgeView extends FrameLayout {


    RightListener rightListener;
    LeftListener leftListener;
    TopListener topListener;
    BottomListener bottomListener;

    public BaseEdgeView(Context context) {
        super(context);
    }

    public BaseEdgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseEdgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRightListener(RightListener keyDownListener) {
        this.rightListener = keyDownListener;
    }

    public void setLeftListener(LeftListener keyDownListener) {
        this.leftListener = keyDownListener;
    }

    public void setTopListener(TopListener keyDownListener) {
        this.topListener = keyDownListener;
    }

    public void setBottomListener(BottomListener keyDownListener) {
        this.bottomListener = keyDownListener;
    }

    public interface RightListener {

        boolean onRight();
    }

    public interface TopListener {
        boolean onTop();
    }

    public interface LeftListener {

        boolean onLeft();
    }

    public interface BottomListener {

        boolean onBottom();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (getChildCount() > 0) {
                View view = getFocusedChild();
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (view.getTag(R.integer.edge_view_key_of_is_top) != null && topListener != null) {
                        return topListener.onTop();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

                    if (view.getTag(R.integer.edge_view_key_of_is_Left) != null && leftListener != null) {
                        return leftListener.onLeft();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (view.getTag(R.integer.edge_view_key_of_is_right) != null && rightListener != null) {
                        return rightListener.onRight();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (view.getTag(R.integer.edge_view_key_of_is_bottom) != null && bottomListener != null) {
                        return bottomListener.onBottom();
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
