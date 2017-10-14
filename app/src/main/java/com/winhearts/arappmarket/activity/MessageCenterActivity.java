package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.adapter.BaseHeaderRecyclerAdapter;
import com.winhearts.arappmarket.utils.adapter.MessageContentRecyclerAdapter;
import com.winhearts.arappmarket.model.RelateReplyEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsNewReply;

import java.util.ArrayList;

/**
 * 消息中心页面
 * Created by suxq on 2017/3/8.
 */

public class MessageCenterActivity extends BaseActivity{

    private static final String TAG = "MessageCenterActivity";

    private Context mContext;
    private RecyclerView mMessageContentRcv;
    private MessageContentRecyclerAdapter mMessageContentAdapter;
    private BaseHeaderRecyclerAdapter.OnItemClickListener mContentItemClickListener;
    private ArrayList<RelateReplyEntity> mReplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        mContext = this;
        initVariable();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initVariable() {
        mReplyList = new ArrayList<>();
        mReplyList.addAll(ModeLevelAmsNewReply.restoreData());
        mMessageContentAdapter = new MessageContentRecyclerAdapter(this, mReplyList);
        mMessageContentAdapter.setOnItemClickListener(mContentItemClickListener);

        updateMessageReadState();
    }

    private void initView() {
        mMessageContentRcv = (RecyclerView) findViewById(R.id.rcv_message_center_content);
        mMessageContentRcv.setLayoutManager(new LinearLayoutManager(this));
        mMessageContentRcv.setAdapter(mMessageContentAdapter);
    }

    /**
     * 更改未读消息为已读状态，即清零未读消息数量
     */
    private void updateMessageReadState() {
        ModeLevelAmsNewReply.resetUnreadCount(0);
    }


}
