package com.winhearts.arappmarket.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.Topic;

/**
 * 专题列表
 * Created by lmh on 2016/4/25.
 */
public class TopicListView extends RelativeLayout {
    private SimpleDraweeView sdIcon;
    private MarqueeTextView tvName;

    public TopicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TopicListView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setClipChildren(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_topic_list, this);
        sdIcon = (SimpleDraweeView) view.findViewById(R.id.sd_topic_list_icon);
        CommonHierarchy.setHierarchyCardPic(sdIcon);
        tvName = (MarqueeTextView) view.findViewById(R.id.tv_topic_list_name);
    }

    public void setData(Topic topic, int width, int height) {
        String icon = topic.getIcon();
        if (!TextUtils.isEmpty(icon)) {
            CommonHierarchy.showThumb(getContext(), Uri.parse(icon), sdIcon, width, height);
//            sdIcon.setImageURI(Uri.parse(icon));
        }
        tvName.setText(topic.getName());
    }
}
