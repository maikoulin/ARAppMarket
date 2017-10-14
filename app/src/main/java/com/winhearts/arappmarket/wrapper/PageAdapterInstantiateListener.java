package com.winhearts.arappmarket.wrapper;


import com.winhearts.arappmarket.view.HorizontalLayout;

/**
 * Created by lmh on 2016/3/2.
 */
public interface PageAdapterInstantiateListener {
    /**
     * 添加页内容
     *
     * @param position       需要加载第几页
     * @param horizontalLayout 加载的布局
     */
    void addInstantiateItem(int position, HorizontalLayout horizontalLayout);
}
