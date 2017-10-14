package com.winhearts.arappmarket.network;

/**
 * Created by lmh on 2015/11/16.
 */
public abstract class UIDataListener<T> {
    public abstract void onDataChanged(T data);

    public abstract void onErrorHappened(int errorCode, Exception errorMessage);

    public void onStringChanged(String src) {
    }

    public abstract void onVolleyError(int errorCode, Exception errorMessage);
}
