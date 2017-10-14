package com.winhearts.arappmarket.fragment;

/**
 * fragment基类
 * 封装了通用的方法，如tab点击向下到verLayout 布局
 */
public abstract class MovableFragment extends BaseFragment {
    /**
     * 注意原来的代码 这里把 focusMoveToLeft 和 focusTab2Down 都用在这里了
     */
    public abstract void focusMoveToLeft();

    /**
     * tab点击向下到verLayout 布局
     */
    public void focusTab2Down() {

    }

    public abstract void focusMoveToRight();

    public void onScrollFirstPositionNoFocus() {

    }

    /**
     * 清理保存的当前屏蔽记录
     */
    public void clearCurrentScreen() {

    }

    public void onScrollLastPositionNoFocus() {

    }

}
