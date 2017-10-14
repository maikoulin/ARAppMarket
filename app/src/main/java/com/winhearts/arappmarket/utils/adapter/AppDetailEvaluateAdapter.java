package com.winhearts.arappmarket.utils.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.winhearts.arappmarket.model.EvaluateEntity;
import com.winhearts.arappmarket.view.ScoreView;
import com.winhearts.arappmarket.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 应用详情界面，评论列表适配器
 * Created by lmh on 2015/10/29.
 */
public class AppDetailEvaluateAdapter extends BaseRecyclerAdapter<EvaluateEntity.EvaluateContent> {
    private List<EvaluateEntity.EvaluateContent> comments;
    private boolean isShowAll;
    private SimpleDateFormat formatter;

    public AppDetailEvaluateAdapter(List<EvaluateEntity.EvaluateContent> comments, boolean isShowAll) {
        super(comments);
        this.comments = comments;
        this.isShowAll = isShowAll;
        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    }

    @Override
    int getLayoutId(int viewType) {
        return R.layout.adapter_appdetail_evaluate_item;
    }

    @Override
    public int getItemCount() {
        if (comments.size() != 0) {
            if (!isShowAll && comments.size() > 3) {
                return 3;
            } else {
                return comments.size();
            }
        } else {
            return 1;
        }

    }

    @Override
    void convert(VH holder, final EvaluateEntity.EvaluateContent data, final int position) {
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(true);
        ScoreView svScore = holder.getView(R.id.tv_app_detail_evaluate_item_score);
        TextView tvReplyCount = holder.getView(R.id.tv_app_detail_evaluate_item_reply);
        TextView tvContent = holder.getView(R.id.tv_app_detail_evaluate_item_content);
        TextView tvNickname = holder.getView(R.id.tv_app_detail_evaluate_item_nickname);
        TextView tvData = holder.getView(R.id.tv_app_detail_evaluate_item_date);
        if (comments.size() != 0) {
            tvData.setVisibility(View.VISIBLE);
            svScore.setVisibility(View.VISIBLE);
            tvNickname.setVisibility(View.VISIBLE);
            long time = Long.valueOf(comments.get(position).getTime());
            tvData.setText(formatter.format(new Date(time)));
            String nickname = comments.get(position).getNickName();
            String showNickname = nickname.substring(0, 1) + "***" + nickname.substring(nickname.length() - 1);
            tvNickname.setText(showNickname);
            tvContent.setText(comments.get(position).getContent());
            float score = Float.valueOf(comments.get(position).getStar());
            try {
                svScore.setScore(score, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String replyCount = comments.get(position).getReplyCount();
            if (TextUtils.isEmpty(replyCount) || Integer.valueOf(replyCount) == 0) {
                tvReplyCount.setVisibility(View.INVISIBLE);
            } else {
                tvReplyCount.setVisibility(View.VISIBLE);
                tvReplyCount.setText(replyCount + "条回复 >");
            }
            holder.getView(R.id.rl_app_detail_evaluate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, data, position);
                    }
                }
            });
        } else {
            tvData.setVisibility(View.INVISIBLE);
            svScore.setVisibility(View.INVISIBLE);
            tvNickname.setVisibility(View.INVISIBLE);
            tvReplyCount.setVisibility(View.INVISIBLE);
            tvContent.setText("暂时没有评论 ！");
        }
    }
}
