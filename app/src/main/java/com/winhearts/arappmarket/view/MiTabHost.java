package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TabHost;

import java.lang.reflect.Method;

/**
 * tablayot 子项
 */
public class MiTabHost extends TabHost {
    public MiTabHost(Context context) {
        super(context, null);
    }
    public MiTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if ( event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            //Utils.playKeySound(this.getCurrentView(), Utils.SOUND_KEYSTONE_KEY);
            try {
                Class osSystem = Class.forName("com.xiaomi.mitv.api.system.ExpandStatusBarController");
                Method showStatusBar = osSystem.getMethod("show", Context.class);
                showStatusBar.invoke(osSystem, this.getContext());
            } catch (Exception e) {}

//            Intent intent = new Intent("com.tv.ui.metro.ACTION_SHOW_STATUS_BAR");
//            intent.putExtra("show", true);
//            getContext().sendBroadcast(intent);

            //return true;
        }
        return super.dispatchKeyEvent(event);
    }
    
    
}
