package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.utils.adapter.SearchResultAdapter;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.QuerySoftwaresRecomment;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.modellevel.ModeLevelAms;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.BaseEdgeView;
import com.winhearts.arappmarket.view.BlowUpUtil;
import com.winhearts.arappmarket.view.CusGridView;
import com.winhearts.arappmarket.view.SoftwaresRecommentView;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用搜索
 */
public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private String[] content;
    private TextView tvKeyWord;
    private StringBuffer keyStrBuff = new StringBuffer();
    //    private int width;
    //    private int height
    private ArrayList<SoftwareInfo> searchResultInfoList = new ArrayList<SoftwareInfo>();

    private View resultLayout;
    private CusGridView keyInputView;

    private TextView searchText;
    private ImageView clearView;
    private ImageView backspaceView;

    private Context mContext;
    private RelativeLayout searchLoading;

    private View searchNullResultView;

    private SearchResultAdapter searchResultAdapter;
    private CusGridView searchResultView;

    private View recommendationView;

    private View netErrorView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));

        searchLoading = (RelativeLayout) this.findViewById(R.id.rl_search_loading);

        searchText = (TextView) findViewById(R.id.result_text);

        recommendationView = findViewById(R.id.ll_search_recommendation);
        netErrorView = findViewById(R.id.ll_net_error);

        searchResultView = (CusGridView) findViewById(R.id.cus_grid_search_result);
        searchNullResultView = findViewById(R.id.ll_search_result_none);
        resultLayout = findViewById(R.id.ll_result);

        initResultView();
        initInpoutView();
        initRecommentView();

        ModeLevelAms.querySearchRecomment(this, TAG, new ModeUserErrorCode<QuerySoftwaresRecomment>() {
            @Override
            public void onJsonSuccess(QuerySoftwaresRecomment softwares) {
                ((SoftwaresRecommentView) findViewById(R.id.softRecommentView_search)).setData(softwares);
            }

            @Override
            public void onRequestFail(int code, Throwable e) {

            }
        });

    }

    private void initRecommentView() {
        ((SoftwaresRecommentView) findViewById(R.id.softRecommentView_search)).setTopListener(new BaseEdgeView.TopListener() {
            @Override
            public boolean onTop() {
                return true;
            }
        });
        ((SoftwaresRecommentView) findViewById(R.id.softRecommentView_search)).setBottomListener(new BaseEdgeView.BottomListener() {

            @Override
            public boolean onBottom() {
                return true;
            }
        });
        ((SoftwaresRecommentView) findViewById(R.id.softRecommentView_search)).setRightListener(new BaseEdgeView.RightListener() {
            @Override
            public boolean onRight() {
                return true;
            }
        });
    }

    private void initResultView() {
        int width = ScreenUtil.getScreenWidth(this);

        searchResultView
                .setVerticalSpacing(ScreenUtil.dip2px(this, 10));
        searchResultView
                .setHorizontalSpacing(ScreenUtil.dip2px(this, 15));
        int itemWidth = ScreenUtil.dip2px(this, width / 19);
        int itemHeight = ScreenUtil.dip2px(this, width / 19);
        searchResultAdapter = new SearchResultAdapter(this, searchResultInfoList, itemWidth,
                itemHeight);

        searchResultView.init(4);
        searchResultView.setOnItemFoucsChangeListener(onDefalutFocusChangeListener);
        searchResultView.setOnItemClickListener(mResultItemClickListener);
        searchResultView.setAdapter(searchResultAdapter);

        searchResultView.setTopListener(new CusGridView.TopListener() {
            @Override
            public boolean onTop() {
                return true;
            }
        });
        searchResultView.setBottomListener(new CusGridView.BottomListener() {
            @Override
            public boolean onBottom() {
                return true;
            }
        });
    }


    private void initInpoutView() {
        tvKeyWord = (TextView) findViewById(R.id.tv_search_input);

        clearView = (ImageView) findViewById(R.id.clear);
        backspaceView = (ImageView) findViewById(R.id.backspace);

        clearView.setOnClickListener(mOnClickListener);
        backspaceView.setOnClickListener(mOnClickListener);

        content = getResources().getStringArray(R.array.search_key);

        keyInputView = (CusGridView) findViewById(R.id.cus_grid_search_key_input);
        keyInputView.init(6);
        keyInputView.setItemH(-1);
        keyInputView.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer index = (Integer) v.getTag();
                keyStrBuff.append(content[index]);
                tvKeyWord.setText(keyStrBuff.toString());
                querySoftwares(SearchActivity.this,
                        keyStrBuff.toString());
            }
        });
        keyInputView.setAdapter(new GridViewAdapter());
        keyInputView.setRightListener(new CusGridView.RightListener() {

            @Override
            public boolean onRight() {
                if (searchResultInfoList.size() > 0 || recommendationView.getVisibility() == View.VISIBLE) {
                    return false;
                }
                return true;
            }
        });
        if (keyInputView.getChildAt(0) != null) {
            keyInputView.getChildAt(0).requestFocus();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (keyInputView.getChildAt(0) != null) {
                        keyInputView.getChildAt(0).requestFocus();
                    }
                }
            }, 100);
        }
    }

    class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return content.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = LayoutInflater.from(SearchActivity.this);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.adapter_input, null);
                viewHolder = new ViewHolder();
                viewHolder.episode = (TextView) convertView
                        .findViewById(R.id.episode);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.episode.setText(content[position]);
            return convertView;
        }

    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.backspace) {
                if (keyStrBuff.length() == 0) {
                    return;
                }
                keyStrBuff.delete(keyStrBuff.length() - 1, keyStrBuff.length());
                tvKeyWord.setText(keyStrBuff.toString());
                querySoftwares(SearchActivity.this,
                        keyStrBuff.toString());
            } else {
                keyStrBuff.delete(0, keyStrBuff.length());
                tvKeyWord.setText(keyStrBuff.toString());
                querySoftwares(SearchActivity.this,
                        keyStrBuff.toString());
            }

        }

    };

    class ViewHolder {
        private TextView episode;
    }

    public void querySoftwares(final Context mContext,
                               final String keyword) {
        if (keyword.length() == 0) {

            clearSearchResult();
            return;

        }
        recommendationView.setVisibility(View.GONE);
        searchLoading.setVisibility(View.VISIBLE);
        searchResultView.setVisibility(View.GONE);
        searchNullResultView.setVisibility(View.GONE);
        netErrorView.setVisibility(View.GONE);

        VolleyQueueController.getInstance().cancelAll(TAG);

        ModeLevelAms.querySoftwares(mContext, TAG, keyword, new ModeUserErrorCode<Softwares>() {
            @Override
            public void onJsonSuccess(Softwares softwares) {
                searchLoading.setVisibility(View.GONE);
                if (keyStrBuff.length() == 0) {
                    return;
                }
                List<SoftwareInfo> softWares = softwares
                        .getSoftwares();
                searchResultInfoList.clear();
                if (softWares != null) {
                    searchResultInfoList.addAll(softWares);
                }
                // scrollView.setVisibility(View.INVISIBLE);
                resultLayout.setVisibility(View.VISIBLE);

                SpannableString spannableString = new SpannableString("搜索结果: " + searchResultInfoList.size()
                        + "条");
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#e1aa57")), 5,
                        6 + (searchResultInfoList.size() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                searchText.setText(spannableString);

                searchResultAdapter.notifyDataSetChanged();

                if (searchResultInfoList.size() > 0) {
                    searchNullResultView.setVisibility(View.GONE);
                    searchResultView.setVisibility(View.VISIBLE);
                    recommendationView.setVisibility(View.GONE);
                } else {
                    searchNullResultView.setVisibility(View.VISIBLE);
                    searchResultView.setVisibility(View.GONE);
                    recommendationView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                searchLoading.setVisibility(View.GONE);
                netErrorView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void clearSearchResult() {

        searchResultInfoList.clear();
        resultLayout.setVisibility(View.GONE);
        searchNullResultView.setVisibility(View.GONE);
        searchResultView.setVisibility(View.GONE);
        recommendationView.setVisibility(View.VISIBLE);
        netErrorView.setVisibility(View.GONE);
        searchLoading.setVisibility(View.GONE);
        searchResultAdapter.notifyDataSetChanged();

    }

    OnClickListener mResultItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Integer position = (Integer) v.getTag();
            DownloadPath downloadPath = new DownloadPath();
            downloadPath.setLayoutId(PrefNormalUtils.getString(mContext, PrefNormalUtils.LAYOUT_ID, ""));
            downloadPath.setMenuId("-1");
            SoftwareInfo softwareInfo = searchResultInfoList.get(position);
            String packageName = softwareInfo.getPackageName();
            Intent intent = new Intent(mContext, AppDetailActivity.class);
            downloadPath.setModulePath(packageName);
            intent.putExtra("downloadPath", downloadPath);
            intent.putExtra("packageName", packageName);
            mContext.startActivity(intent);
        }
    };
    /*
     * public void getHistory() { ArrayList<String> historyList ;
	 * historyWords.clear(); historyWords.addAll(historyList); LayoutInflater
	 * layoutInflater = LayoutInflater.from(this); for (int i = 0; i <
	 * historyWords.size(); i++) { String hotWord = historyWords.get(i); View
	 * view = layoutInflater.inflate(R.layout.fix_btn, null); Button item =
	 * (Button) view.findViewById(R.id.fix_btn); item.setText(hotWord);
	 * item.setTag(hotWord); item.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { String word = (String)
	 * v.getTag(); search(word); } }); mHistoryGridView.addView(view, new
	 * FixLayout.LayoutParams(10, 10)); } }
	 */

    BlowUpUtil blowUpUtil;

    OnFocusChangeListener onDefalutFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (blowUpUtil == null) {
                    blowUpUtil = new BlowUpUtil(SearchActivity.this);
                    blowUpUtil.setFocusDrawable(R.drawable.comm_bg_card_focus);
                }
                blowUpUtil.setScaleUp(v);
            } else {
                if (blowUpUtil == null) {
                    blowUpUtil = new BlowUpUtil(SearchActivity.this);
                    blowUpUtil.setFocusDrawable(R.drawable.comm_bg_card_focus);
                }
                blowUpUtil.setScaleDown(v);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (keyStrBuff.length() == 0) {
                return super.onKeyDown(keyCode, event);
            }
            keyStrBuff.delete(keyStrBuff.length() - 1, keyStrBuff.length());
            tvKeyWord.setText(keyStrBuff.toString());
            querySoftwares(SearchActivity.this,
                    keyStrBuff.toString());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyQueueController.getInstance().cancelAll(TAG);
    }
}
