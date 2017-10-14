package com.winhearts.arappmarket.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * Description:通用标题栏
 * Created by lmh on 2016/4/13.
 */
public class CommonTitle extends FrameLayout {
    private String title = "";
    TextView nameTextView;

    public CommonTitle(Context context) {
        super(context);
        init();
    }

    public CommonTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonTitle);
        title = a.getString(R.styleable.CommonTitle_title_name);
        init();
    }

    public CommonTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.comm_title, this, false);
        addView(view);
        nameTextView = (TextView) view.findViewById(R.id.tv_module_title);
        nameTextView.setText(title);
    }

    public void setTitle(String name) {
        nameTextView.setText(name);
    }
}
