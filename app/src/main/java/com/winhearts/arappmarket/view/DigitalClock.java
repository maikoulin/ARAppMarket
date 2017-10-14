package com.winhearts.arappmarket.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 自定认时间样式
 */
public class DigitalClock extends TextView {
    private final static String TAG = "DigitalClock";

    private Runnable mTicker;
    private Handler mHandler;

    //	private SimpleDateFormat dateformat2 = new SimpleDateFormat(" HH:mm  E");
    private SimpleDateFormat dateformat2 = new SimpleDateFormat(" HH:mm ", Locale.getDefault());

    private boolean mTickerStopped = false;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                String a2 = dateformat2.format(new Date());
                setText(a2);
                invalidate();
                long now = SystemClock.uptimeMillis();
                // long next = now + (1000 - now % 1000);
                long next = now + (1000 - System.currentTimeMillis() % 1000);

                // TODO
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

}
