package com.winhearts.arappmarket.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.wrapper.PageAdapterInstantiateListener;
import com.winhearts.arappmarket.view.HorizontalLayout;

/**
 * 通用的PagerAdapter
 * Created by lmh on 2016/3/1.
 */
public class CommonPagerAdapter extends PagerAdapter {
    private int pageCount;
    private Context context;
    private PageAdapterInstantiateListener listener;
    private int drawableId = R.drawable.comm_bg_card_focus;

    public CommonPagerAdapter(Context context, int pageCount, PageAdapterInstantiateListener listener) {
        this.pageCount = pageCount;
        this.context = context;
        this.listener = listener;
    }

    public void setFocusDrawable(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View mView = LayoutInflater.from(context).inflate(R.layout.advert_item_layout, null);
        HorizontalLayout mAdvertLayout = (HorizontalLayout) mView.findViewById(R.id.metrolayout);
        mAdvertLayout.setFocusDrawable(drawableId);
        mAdvertLayout.setPageCount(pageCount);
        listener.addInstantiateItem(position, mAdvertLayout);
        container.addView(mView);
        mView.setTag(position);
        return mView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
