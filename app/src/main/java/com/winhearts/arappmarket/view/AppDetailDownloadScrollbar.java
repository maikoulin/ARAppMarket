package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;


import com.winhearts.arappmarket.R;

import java.text.DecimalFormat;

/**
 * 应用详情页进度条
 * Created by lmh on 2017/5/26.
 */

public class AppDetailDownloadScrollbar extends ProgressBar {
    private String text = "";
    private Paint mPaint;
    private int color = Color.WHITE;
    private Rect rect;

    public AppDetailDownloadScrollbar(Context context) {
        super(context);
        initText();
    }

    public AppDetailDownloadScrollbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public AppDetailDownloadScrollbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setColor(color);
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 3) - rect.centerY();
        canvas.drawText(text, x, y, this.mPaint);
    }

    //初始化，画笔
    private void initText() {
        this.mPaint = new Paint();
        mPaint.setTextSize(getContext().getResources().getDimensionPixelSize(R.dimen.edit_text_size));
        rect = new Rect();

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String content) {
        this.text = content;
        invalidate();
    }

    //设置文字内容
    public void setText(int progress) {

        float index = (progress * 100f) / this.getMax();
        String valueFormat = new DecimalFormat("#").format(index);// 转换为字符串 不要小数
        this.text = String.valueOf(valueFormat) + "%";

        invalidate();
    }
}
