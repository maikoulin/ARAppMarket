package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.ScreenUtil;


/**
 * 儿童锁密码输入选择框
 *
 * @author lmh
 */
public class ChildrenLockInputPassWordView extends LinearLayout {


    private Context context;

    private TextSwitcher textSwitcher;
    private ImageView upImg;
    private ImageView downImg;
    private int index = 0;

    public ChildrenLockInputPassWordView(Context context) {
        super(context);
        initView(context);
    }

    public ChildrenLockInputPassWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChildrenLockInputPassWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    /**
     * 根据type初始化
     */
    private void initView(Context context) {
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_children_lock_input_item, this);
        textSwitcher = (TextSwitcher) findViewById(R.id.ts_children_lock_input_item);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            //这里 用来创建内部的视图，这里创建TextView，用来显示文字
            public View makeView() {
                TextView tv = new TextView(getContext());
                //设置文字大小
                tv.setTextSize(40);
                //设置文字 颜色
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ScreenUtil.dip2px(ChildrenLockInputPassWordView.this.context, 70), ScreenUtil.dip2px(ChildrenLockInputPassWordView.this.context, 80));
                tv.setLayoutParams(layoutParams);
                return tv;
            }
        });
        textSwitcher.setText(index + "");
        upImg = (ImageView) findViewById(R.id.iv_children_lock_input_item_up);
        downImg = (ImageView) findViewById(R.id.iv_children_lock_input_item_down);
        textSwitcher.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        upImg.setSelected(true);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        downImg.setSelected(true);
                    }
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        index--;
                        upImg.setSelected(false);
                        textSwitcher.setOutAnimation(ChildrenLockInputPassWordView.this.context, R.anim.children_lock_input_item_out_up);
                        textSwitcher.setInAnimation(ChildrenLockInputPassWordView.this.context, R.anim.children_lock_input_item_in_up);

                        if (index > 9) {
                            index = 0;
                        } else if (index < 0) {
                            index = 9;
                        }
                        textSwitcher.setText(index + "");
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        index++;
                        downImg.setSelected(false);
                        textSwitcher.setOutAnimation(ChildrenLockInputPassWordView.this.context, R.anim.children_lock_input_item_out_down);
                        textSwitcher.setInAnimation(ChildrenLockInputPassWordView.this.context, R.anim.children_lock_input_item_in_down);

                        if (index > 9) {
                            index = 0;
                        } else if (index < 0) {
                            index = 9;
                        }
                        textSwitcher.setText(index + "");
                    }
                }
                return false;
            }
        });
        textSwitcher.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    upImg.setVisibility(View.VISIBLE);
                    downImg.setVisibility(View.VISIBLE);
                } else {
                    upImg.setVisibility(View.INVISIBLE);
                    downImg.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    public String getCurIndexValue() {
        return index + "";
    }

    public void reSetIndexValue() {
        index = 0;
        textSwitcher.setInAnimation(null);
        textSwitcher.setOutAnimation(null);
        textSwitcher.setText("0");
    }


}
