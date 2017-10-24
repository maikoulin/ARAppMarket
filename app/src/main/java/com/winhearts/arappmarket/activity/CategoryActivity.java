package com.winhearts.arappmarket.activity;

import android.os.Bundle;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.utils.LogDebugUtil;

/**
 * Created by lmh on 2017/10/24.
 */

public class CategoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Bundle bundle = getIntent().getExtras();
        MenuItem item = (MenuItem) bundle.getSerializable("message");
        LogDebugUtil.e("MenuItem", item.toString());
    }
}
