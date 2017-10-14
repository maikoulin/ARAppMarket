package com.winhearts.arappmarket.logic;

import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.utils.cust.LayoutInfoObserver;

import java.util.ArrayList;

/**
 * 用于布局的更新回调，观察者
 * Created by lmh on 2017/5/23.
 */

public class LayoutInfoSubject {
    private static ArrayList<LayoutInfoObserver> mObservers = new ArrayList<>();

    public static synchronized boolean registerObserve(LayoutInfoObserver observer) {
        return mObservers.contains(observer) || mObservers.add(observer);
    }

    public static synchronized boolean unregisterObserve(LayoutInfoObserver observer) {
        return mObservers.remove(observer);
    }

    public static synchronized void onSuccess(Layout layout) {
        for (LayoutInfoObserver mObserver : mObservers) {
            mObserver.onSuccess(layout);
        }
    }

    public static synchronized void onFail(int code, String description) {
        for (LayoutInfoObserver mObserver : mObservers) {
            mObserver.onFail(code, description);
        }
    }
}
