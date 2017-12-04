package com.winhearts.arappmarket.activity;

import android.os.Bundle;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.fragment.MainSelfFragment;

/**
 * Created by lmh on 2017/9/19.
 */

public class BoutiqueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique);
        MainSelfFragment fragment= new MainSelfFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commitAllowingStateLoss();
         // 修改提交测试 注释
    }
}
