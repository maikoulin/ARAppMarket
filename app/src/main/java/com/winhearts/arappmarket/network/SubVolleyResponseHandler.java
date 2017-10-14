
package com.winhearts.arappmarket.network;

import android.content.Context;

import com.android.volley.VolleyError;

import java.lang.reflect.Type;

/**
 * Created by lmh on 2015/11/13.
 */
public class SubVolleyResponseHandler<T> extends VolleyResponseHandler<T> {


    public SubVolleyResponseHandler(Type mType, Context context) {
        super(mType);
    }

    public SubVolleyResponseHandler(Type mType) {
        super(mType);
    }

    @Override
    protected void disposeVolleyError(VolleyError error) {
        if (error == null) {
            onVolleyError(REQUEST_FAIL, new Exception("VolleyError==null"));
        } else {
            if (error.networkResponse != null) {
                byte[] htmlBodyBytes = error.networkResponse.data;
                String errorStr = new String(htmlBodyBytes);
                if (errorStr.contains("NoConnectionError")) {
                    onVolleyError(NETWORK_ERROR, new Exception(errorStr));
                } else {
                    onVolleyError(REQUEST_FAIL, new Exception(errorStr));
                }
            } else {
                onVolleyError(REQUEST_FAIL, error);
            }
        }
    }

    @Override
    protected void disposeResponse(T response) {
        notifyDataChanged(response);
    }
}
