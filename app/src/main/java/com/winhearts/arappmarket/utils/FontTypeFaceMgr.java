package com.winhearts.arappmarket.utils;

import android.graphics.Typeface;

import com.winhearts.arappmarket.activity.VpnStoreApplication;

import java.util.HashMap;

/**
 * Description: 获取字体
 * Created by lmh on 2016/3/21.
 */
public class FontTypeFaceMgr {
    private HashMap<String, Typeface> mTypefaces;
    static private FontTypeFaceMgr fontTypeFaceMgr;

    static public FontTypeFaceMgr getInstance(){
        if (fontTypeFaceMgr == null){
            fontTypeFaceMgr = new FontTypeFaceMgr();
        }
        return fontTypeFaceMgr;
    }

    private FontTypeFaceMgr() {
        mTypefaces = new HashMap<String, Typeface>();
    }

    public Typeface getTypefaceFromAssets(String aTTFName) {
        if (mTypefaces.containsKey(aTTFName)) {
            return mTypefaces.get(aTTFName);
        } else {

            Typeface font;
            try {
                // font = Typeface.createFromFile("/system/fonts/" + aTTFName);
                font = Typeface.createFromAsset(VpnStoreApplication.app.getAssets(), aTTFName);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            mTypefaces.put(aTTFName, font);
            return font;
        }
    }

    public Typeface getMSYHTypefaceFromAssets() {
        String aTTFName = "fonts/MSYH.TTF";
        if (mTypefaces.containsKey(aTTFName)) {
            return mTypefaces.get(aTTFName);
        } else {

            Typeface font;
            try {
                // font = Typeface.createFromFile("/system/fonts/" + aTTFName);
                font = Typeface.createFromAsset(VpnStoreApplication.app.getAssets(), aTTFName);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            mTypefaces.put(aTTFName, font);
            return font;
        }
    }
}
