package com.winhearts.arappmarket.utils.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.model.ReplyResEntity;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 应用详情页回复适配器
 * Created by lmh on 2017/3/14.
 */

public class AppDetailReplyAdapter extends BaseRecyclerAdapter<ReplyResEntity.ReplyList> {
    private SimpleDateFormat dateFormat;
    private long currentTime;
    private int mFocusIndex = -1;//获取焦点item位置

    public AppDetailReplyAdapter(List<ReplyResEntity.ReplyList> datas) {
        super(datas);
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        currentTime = Util.getAmendCurrentTimeMs(Util.getUrl(VpnStoreApplication.getApp(), ""));
    }

    public void setFocusIndex(int focusIndex) {
        mFocusIndex = focusIndex;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.adapter_app_detail_reply;
    }

    @Override
    void convert(final VH holder, final ReplyResEntity.ReplyList data, final int position) {
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, data, position);
                }
            }
        });
        TextView replyName = holder.getView(R.id.tv_reply_item_name);
        String fromUser = data.getFromUser();
        String toUser = data.getToUser();
        String showNickname;
        String briefFormUser = fromUser.equals("我") ? fromUser : (fromUser.substring(0, 1) + "***" + fromUser.substring(fromUser.length() - 1));
        if (TextUtils.isEmpty(toUser)) {
            showNickname = briefFormUser;
        } else {
            String briefToUser = toUser.equals("我") ? toUser : (toUser.substring(0, 1) + "***" + toUser.substring(toUser.length() - 1));
            showNickname = briefFormUser + "回复" + briefToUser;
        }
        replyName.setText(showNickname);
        ((TextView) (holder.getView(R.id.tv_reply_item_time))).setText(getDate(data.getTime()));
        ((TextView) (holder.getView(R.id.tv_reply_item_content))).setText(data.getContent());
        if (mFocusIndex == position) {
            holder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.requestFocus();
                    mFocusIndex = -1;
                }
            }, 300);

        }
    }

    private String getDate(String time) {

        try {
            String replyTime = dateFormat.format(new Date(Long.valueOf(time)));
            String strCurrentTime = dateFormat.format(new Date(currentTime));
            String replyDataTime = replyTime.substring(0, 11);
            String currentDateTime = strCurrentTime.substring(0, 11);
            String replyYear = replyTime.substring(0, 5);
            String currentYear = strCurrentTime.substring(0, 5);
            if (replyDataTime.equals(currentDateTime)) {
                return replyTime.substring(11);
            } else if (replyYear.equals(currentYear)) {
                return replyTime.substring(5, 11);
            } else {
                return replyDataTime;
            }
        } catch (NumberFormatException e) {
            LogDebugUtil.e("NumberFormatException", e.getMessage());
            return "2017年01月01日";
        }
    }
}
