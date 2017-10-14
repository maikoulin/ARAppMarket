package com.winhearts.arappmarket.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.winhearts.arappmarket.utils.ActivityStack;

/**
 * APP基础类
 */
public class BaseMostActivity extends FragmentActivity {
    private boolean isOneShoot = true;


    //记录每个打开的acitivity。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        ActivityStack.getActivityStack().pushActivity(this);
    }


    @Override
    protected void onDestroy() {
        ActivityStack.getActivityStack().pullActivity(this);
        super.onDestroy();
    }

}
 