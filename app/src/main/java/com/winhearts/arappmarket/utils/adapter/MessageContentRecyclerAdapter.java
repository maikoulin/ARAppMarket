package com.winhearts.arappmarket.utils.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.RelateReplyEntity;
import com.winhearts.arappmarket.utils.DateUtil;

import java.util.ArrayList;

/**
 * 消息中心的新回复消息列表项的适配器
 * Created by suxq on 2017/3/9.
 */

public class MessageContentRecyclerAdapter extends BaseHeaderRecyclerAdapter<RelateReplyEntity> {

    private static final String TAG = MessageContentRecyclerAdapter.class.getSimpleName();
    private Context mContext;

    public MessageContentRecyclerAdapter(Context context, ArrayList<RelateReplyEntity> datas) {
        mContext = context;
        addDatas(datas);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view;
        if (mDatas.size() == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_message_center_none, parent, false);
            view.setFocusable(false);
            return new Holder(view);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.item_message_center_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        int size = mDatas.size();
        return size == 0 ? 1 : size;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, RelateReplyEntity data) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.replyTimeTv.setText(DateUtil.millis2String(data.getTime()));
            holder.replyContentTv.setText(data.getContent());
            holder.replyerTv.setText(data.getFormat());
        }
    }

    @Override
    public void onHeadBind(RecyclerView.ViewHolder viewHolder) {
        //没有头布局，不做处理
    }

    private class ViewHolder extends Holder {
        private TextView replyTimeTv;
        private TextView replyerTv;
        private TextView replyContentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            replyTimeTv = (TextView) itemView.findViewById(R.id.tv_message_reply_time);
            replyerTv = (TextView) itemView.findViewById(R.id.tv_message_replyer);
            replyContentTv = (TextView) itemView.findViewById(R.id.tv_message_reply_content);
        }
    }
}
