package com.winhearts.arappmarket.utils.adapter;

import android.widget.BaseAdapter;

import com.winhearts.arappmarket.view.PullToRefreshGridView;

/**
 * 到底加载View的适配器抽象类
 *
 * @author wangmn
 */
public abstract class PullToRefreshAdapter extends BaseAdapter {
    private PullToRefreshGridView gridView;

    public void setGridView(PullToRefreshGridView gridView) {
        this.gridView = gridView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        gridView.onRefreshComplete();//notify the gridview
    }
}
