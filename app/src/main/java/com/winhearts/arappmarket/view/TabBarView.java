package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.ScreenUtil;

import java.util.List;

/**
 * 自定义tagLayout，支付模块
 */
public class TabBarView extends FrameLayout implements OnFocusChangeListener {

    private View ly1;
    private View ly2;
    private View ly3;

    private TextView tab1TextView;
    private TextView tab2TextView;
    private TextView tab3TextView;

    public TextView oldFocusView = null;

    private View line1View;
    private View line2View;
    private View line3View;

    private View oldLineView;
    private OnTabChangeListener tabChangeListener = null;

    private boolean isAbleListener = true;

    private int oldIndex = 0;

    public interface OnTabChangeListener {
        void onTabChange(int index);
    }

    public TabBarView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();

    }

    public TabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView();
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        initView();
    }

    private void initView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_tab_bar, null, false);
        addView(view);

        tab1TextView = (TextView) view.findViewById(R.id.tv_widget_tab_1);
        tab2TextView = (TextView) view.findViewById(R.id.tv_widget_tab_2);
        tab3TextView = (TextView) view.findViewById(R.id.tv_widget_tab_3);
        tab1TextView.setOnFocusChangeListener(this);
        tab2TextView.setOnFocusChangeListener(this);
        tab3TextView.setOnFocusChangeListener(this);

        line1View = view.findViewById(R.id.view_widget_tab1);
        line2View = view.findViewById(R.id.view_widget_tab2);
        line3View = view.findViewById(R.id.view_widget_tab3);

        ly1 = view.findViewById(R.id.ly_widget_tab1);
        ly2 = view.findViewById(R.id.ly_widget_tab2);
        ly3 = view.findViewById(R.id.ly_widget_tab3);

//		tab1TextView.setFocusable(true);

        setText(new String[]{"1", "2", "3"});
    }

    private void setLine(View newLineView) {
        if (oldLineView != null) {
            android.widget.RelativeLayout.LayoutParams oldParams = (android.widget.RelativeLayout.LayoutParams) oldLineView
                    .getLayoutParams();
            oldParams.height = ScreenUtil.dip2px(getContext(), 2);
            oldLineView.requestLayout();

            oldLineView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) newLineView
                .getLayoutParams();
        params.height = ScreenUtil.dip2px(getContext(), 6);
        newLineView.requestLayout();
        newLineView.setBackgroundColor(Color.parseColor("#00aaff"));

        oldLineView = newLineView;
    }

    public void setText(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String title[] = list.toArray(new String[list.size()]);
        setText(title);
    }

    /**
     * @param title
     */
    public void setText(String[] title) {
        TextView[] tv = {tab1TextView, tab2TextView, tab3TextView};
        View[] ly = {ly1, ly2, ly3};

        ly[0].setVisibility(View.GONE);
        ly[1].setVisibility(View.GONE);
        ly[2].setVisibility(View.GONE);

        int count = title.length;
        if (count < 1) {
            return;
        }

        count = count > 3 ? 3 : count;
        for (int i = 0; i < count; i++) {
            if (title[i] == null) {
                ly[i].setVisibility(View.GONE);
            } else {
                ly[i].setVisibility(View.VISIBLE);
            }

            tv[i].setText(title[i]);
        }
    }

    private void onTab(int index) {
        if (tabChangeListener != null && isAbleListener) {
            tabChangeListener.onTabChange(index);
            oldIndex = index;
        }
    }

    public void setOldTab() {
        setTab(oldIndex, false);
    }

    public void setTab(int index) {
        setTab(index, true);
    }

    public TextView getOldView() {
        return oldFocusView;
    }

    public void setTab(int index, boolean isAbleListener) {
        this.isAbleListener = isAbleListener;


        switch (index) {
            case 0:
                tab1TextView.requestFocus();
                oldFocusView = tab1TextView;
                setLine(line1View);

                break;

            case 1:
                tab2TextView.requestFocus();
                oldFocusView = tab2TextView;
                setLine(line2View);
                break;

            case 2:
                tab3TextView.requestFocus();
                oldFocusView = tab3TextView;
                setLine(line3View);
                break;

            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.tv_widget_tab_1:

                if (hasFocus) {
                    oldFocusView = tab1TextView;
                    onTab(0);
                    setLine(line1View);

                }

                break;

            case R.id.tv_widget_tab_2:

                if (hasFocus) {
                    oldFocusView = tab2TextView;
                    onTab(1);
                    setLine(line2View);
                }

                break;

            case R.id.tv_widget_tab_3:
                if (hasFocus) {
                    oldFocusView = tab3TextView;
                    onTab(2);
                    setLine(line3View);
                }

                break;

            default:
                break;
        }
    }

    public void setOnTabChangeListener(OnTabChangeListener tabChangeListener) {
        this.tabChangeListener = tabChangeListener;
    }

}
