package com.winhearts.arappmarket.utils.cust;

import com.winhearts.arappmarket.model.Layout;

/**
 * Created by lmh on 2017/5/23.
 */

public interface LayoutInfoObserver {
    void onSuccess(Layout layout);

    void onFail(int code, String description);
}
