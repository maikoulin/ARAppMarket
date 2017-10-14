package com.winhearts.arappmarket.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.StyleRes;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.adapter.AppDetailEvaluateAdapter;
import com.winhearts.arappmarket.model.EvaluateEntity;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.modellevel.ModelevelEvaluate;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.util.List;
import java.util.Map;

/**
 * 评论列表弹窗
 * Created by lmh on 2017/3/16.
 */

public class EvaluateListDialog extends BaseDialog {
    private RecyclerView rvPopEvaluate;
    private ImageView ivMore;
    private LinearLayoutManager evaluatePopLayoutManager;
    private List<EvaluateEntity.EvaluateContent> comments = null;
    private String mPackageName;
    private String mTotalCount = "0";
    private int pageNo = 0;
    private boolean isRequesting = false;
    private AppDetailEvaluateAdapter appDetailEvaluateAdapter;

    public EvaluateListDialog(Context context) {
        super(context);
    }

    protected EvaluateListDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected EvaluateListDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initView() {
        evaluatePopLayoutManager = new LinearLayoutManager(getContext());
        evaluatePopLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_detail_all_evaluate, null);
        rvPopEvaluate = (RecyclerView) mRootView.findViewById(R.id.rv_pop_app_detail_evaluate);
        rvPopEvaluate.setFocusable(false);
        rvPopEvaluate.setHasFixedSize(true);
        rvPopEvaluate.setLayoutManager(evaluatePopLayoutManager);
        ivMore = (ImageView) mRootView.findViewById(R.id.iv_app_detail_evaluate_more);
        ivMore.setFocusable(false);
        rvPopEvaluate.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                              @Override
                                              public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                  super.onScrolled(recyclerView, dx, dy);
                                                  int totalItemCount = evaluatePopLayoutManager.getItemCount();
                                                  int lastVisibleItem = evaluatePopLayoutManager.findLastVisibleItemPosition();
                                                  if (dy > 0) {
                                                      int visibleThreshold = 1;
                                                      if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                                          ivMore.setVisibility(View.INVISIBLE);
                                                          getEvaluateContent();
                                                      }
                                                  } else if (dy < 0) {
                                                      int visibleThreshold = 0;
                                                      if (!(totalItemCount <= (lastVisibleItem + visibleThreshold))) {
                                                          ivMore.setVisibility(View.VISIBLE);
                                                      }
                                                  }

                                              }
                                          }


        );
    }

    public void setData(List<EvaluateEntity.EvaluateContent> comments, AppDetailEvaluateAdapter.onItemClickListener onItemClickListener) {
        this.comments = comments;
        if (comments.size() != 0) {
            if (comments.size() > 5) {
                ivMore.setVisibility(View.VISIBLE);
            } else {
                ivMore.setVisibility(View.INVISIBLE);
            }
            appDetailEvaluateAdapter = new AppDetailEvaluateAdapter(comments, true);
            appDetailEvaluateAdapter.setOnItemClickListener(onItemClickListener);
            rvPopEvaluate.setAdapter(appDetailEvaluateAdapter);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (isShowing()) {
                        View view = rvPopEvaluate.getChildAt(0);
                        if (view != null) {
                            view.requestFocus();
                        }
                    }
                }

            }, 300);
        } else {
            appDetailEvaluateAdapter = new AppDetailEvaluateAdapter(null, false);
            rvPopEvaluate.setAdapter(appDetailEvaluateAdapter);
        }
        setWindowContentView(mRootView, 620, 620);
    }

    public void setRequestMore(String packageName, String totalCount) {
        mPackageName = packageName;
        mTotalCount = totalCount;
        pageNo = 1;
    }

    public void setRequesting(boolean isRequesting) {
        this.isRequesting = isRequesting;
    }

    //获取评价内容
    private void getEvaluateContent() {
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        int totalCount = Integer.valueOf(mTotalCount.trim());
        if (totalCount > pageNo * 20) {
            ToastUtils.show(getContext(), "正在拼命加载更多...", Toast.LENGTH_SHORT);
            Map<String, String> map = new ArrayMap<>();
            map.put("packageName", mPackageName);
            map.put("pageNo", String.valueOf(pageNo + 1));
            ModelevelEvaluate.getEvaluateList(getContext(), map, new ModeUserErrorCode<EvaluateEntity>() {
                @Override
                public void onJsonSuccess(EvaluateEntity response) {
                    pageNo += 1;
                    isRequesting = false;
                    int size = comments.size();
                    comments.addAll(response.getComments());
                    appDetailEvaluateAdapter.notifyItemRangeInserted(size, comments.size() - size);
                }

                @Override
                public void onRequestFail(int code, Throwable e) {
                    isRequesting = false;
                }
            });

        }
    }
}

