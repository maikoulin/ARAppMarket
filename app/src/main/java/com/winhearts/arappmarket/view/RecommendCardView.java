package com.winhearts.arappmarket.view;


import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.DisplayItem;

/**
 *  推荐应用子项，card样式
 */
public class RecommendCardView extends RelativeLayout {

    private static String TAG = "RecommendCardView";
    protected SimpleDraweeView mBackView;
    private int width;
    private int height;
    String imageUrl;

    public RecommendCardView bindData(DisplayItem _content, int width, int height) {
        imageUrl = _content.imageUrl;
        this.width = width;
        this.height = height;
        bindBackground();

        return this;
    }

    int orientation = 0;

    public RecommendCardView(Context context, int viewType, int x, int y) {
        super(context);
        orientation = viewType;
        init(context);
    }

    public RecommendCardView(Context context) {
        super(context);
        init(context);
    }

    public boolean isSelected() {
        return super.isSelected();
    }

    private void bindBackground() {
        useBgImage();
    }

    private boolean useBgImage() {
        CommonHierarchy.showThumb(getContext(), Uri.parse(imageUrl), mBackView, width, height);
//        mBackView.setImageURI(Uri.parse(imageUrl));
        return true;
    }

    protected void init(Context context) {
        this.setClipChildren(true);
        View view = LayoutInflater.from(context).inflate(R.layout.metro_vertical_item, this);
        mBackView = (SimpleDraweeView) view.findViewById(R.id.back_ground_imageview);
        CommonHierarchy.setHierarchyCardPic(mBackView);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            setSelected(true);
        } else {
            setSelected(false);
        }
    }

}
