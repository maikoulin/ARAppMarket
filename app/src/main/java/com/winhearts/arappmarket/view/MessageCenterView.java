package com.winhearts.arappmarket.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.RelateReplyEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsNewReply;

import java.util.List;

/**
 * 标题栏消息中心图标的控件，包括icon，tip消息预览和小红点未读消息数。
 * Created by suxq on 2017/3/10.
 */

public class MessageCenterView extends RelativeLayout {

    private static final String TAG = "MessageCenterView";
    /**
     * 最近一条消息
     */
    private RelateReplyEntity mLatestReply = null;
    /**
     * 是否有新消息
     */
    private boolean isNewReply;
    /**
     * 未读消息的数量（包括新消息数量 + 之前未读消息数量）
     */
    private int mUnreadReplyCount;

    private ImageView mMessageIconIv;
    private TextView mNewMessageNumberTv;
    private VerticalRollTextView mMessagePreviewTv;
    private ViewGroup mPreviewLayout;
    private String mAccountwinId;
    private boolean mIsPreview;

    public MessageCenterView(@NonNull Context context) {
        this(context, null);
    }

    public MessageCenterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageCenterView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVariable();
        initView();
    }

    private void initVariable() {
        mUnreadReplyCount = 0;
        isNewReply = false;
        mIsPreview = ModeLevelAmsNewReply.getIsPreview();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_message_center, null);
        addView(view);
        mMessageIconIv = (ImageView) view.findViewById(R.id.iv_message_icon);
        mNewMessageNumberTv = (TextView) view.findViewById(R.id.tv_new_message_number);
        mMessagePreviewTv = (VerticalRollTextView) view.findViewById(R.id.vrtv_message_preview);
        mPreviewLayout = (ViewGroup) view.findViewById(R.id.layout_message_preview);
    }

    /**
     * 内容预览的TextView的隐藏与显示控制
     *
     * @param visiable
     */
    public void setMessagePreviewVisiable(int visiable) {
        if (getVisibility() != VISIBLE || !mIsPreview) {
            return;
        }
        if (mPreviewLayout != null && isNewReply && mLatestReply != null) {
            mPreviewLayout.setVisibility(visiable);
        }
    }

    /**
     * 内容预览的TextView，手动切换显示下一行内容
     */
    public void showNextPreviewContent() {
        if (mMessagePreviewTv != null && mPreviewLayout.getVisibility() == VISIBLE) {
            mMessagePreviewTv.showNextContent();
        }
    }

    /**
     * 根据消息的未读/已读状态更新相应view
     */
    public void updateViewVisitity() {
        if (!TextUtils.isEmpty(mAccountwinId) && !mAccountwinId.equals(ConstInfo.accountWinId)) {
            mLatestReply = null;
        }
        mAccountwinId = ConstInfo.accountWinId;
        if (mLatestReply != null) {
            post(updateViewTask());
        } else {
            postDelayed(updateViewTask(), 1000);
        }
    }

    private Runnable updateViewTask() {
        return new Runnable() {
            @Override
            public void run() {
                queryMessageReadState();
                if (isNewReply && mLatestReply != null) {
                    mNewMessageNumberTv.setVisibility(VISIBLE);
                    String newReplyCnt = mUnreadReplyCount > 9 ? "9+" : (mUnreadReplyCount + "");
                    mNewMessageNumberTv.setText(newReplyCnt);
                    String replyContent = mLatestReply.getFormat() + ": " + mLatestReply.getContent();
                    mMessagePreviewTv.setTvText(replyContent);
                } else {
                    mNewMessageNumberTv.setVisibility(GONE);
                    mPreviewLayout.setVisibility(GONE);
                }
            }
        };
    }

    private void queryMessageReadState() {
        mUnreadReplyCount = ModeLevelAmsNewReply.getUnreadCount();
        isNewReply = (mUnreadReplyCount > 0);
        if (isNewReply && mLatestReply == null) {
            List<RelateReplyEntity> reply = ModeLevelAmsNewReply.restoreData();
            if (reply != null && reply.size() > 0) {
                mLatestReply = reply.get(0);
            }
        }
    }
}
