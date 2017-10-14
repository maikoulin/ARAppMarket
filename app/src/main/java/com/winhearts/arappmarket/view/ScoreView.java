package com.winhearts.arappmarket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * 评分view
 */
public class ScoreView extends RelativeLayout {

    Context mContext;
    ImageView start_1;

    ImageView start_2;
    ImageView start_3;
    ImageView start_4;
    ImageView start_5;
    TextView scoreText;
    TextView scoreName;
    int rFull = R.drawable.star;
    int rEmpty = R.drawable.star_blank;
    int rHalf = R.drawable.star_half;
    int rFocus = R.drawable.icon_star_focus;

    public static final int NORMAL = 0;
    public static final int BIG = 1;
    private int mStyle = NORMAL;

    public ScoreView(Context context) {
        super(context);
        this.mContext = context;
        // TODO Auto-generated constructor stub
        initView();
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // 初始UI
        initView();
    }

    public void setStyle(int style) {
        mStyle = style;

        if (mStyle == BIG) {
            rFull = R.drawable.icon_star_full;
            rHalf = R.drawable.icon_star_half;
            rEmpty = R.drawable.icon_star_empty;
        } else {
            rFull = R.drawable.star;
            rHalf = R.drawable.star_half;
            rEmpty = R.drawable.star_blank;
        }
        initView();
    }

    public void initView() {
        LayoutInflater flater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = null;
        if (mStyle == BIG) {
            view = flater.inflate(R.layout.widget_scorebar_big, null, true);
        } else {
            view = flater.inflate(R.layout.widget_scorebar, null, true);
        }

        start_1 = (ImageView) view.findViewById(R.id.start1);
        start_2 = (ImageView) view.findViewById(R.id.start2);
        start_3 = (ImageView) view.findViewById(R.id.start3);
        start_4 = (ImageView) view.findViewById(R.id.start4);
        start_5 = (ImageView) view.findViewById(R.id.start5);
        scoreText = (TextView) view.findViewById(R.id.score_text);
        scoreName = (TextView) view.findViewById(R.id.tv_score_name);
        removeAllViews();
        addView(view);
    }

    public void setScoreNameVisibility(int visibility) {
        scoreName.setVisibility(visibility);
    }

    public void setScore(float score, boolean displayDigit) throws Exception {

        if (score > 5.0f) {
            throw new Exception("score to large");
        }

        int[] resArr = new int[5];
        int scoreInt = (int) score;
        boolean isInt = ((score - scoreInt) == 0);

        for (int i = 0; i < 5; i++) {
            if (i < score) {
                resArr[i] = rFull;
            } else {
                resArr[i] = rEmpty;
            }

            if (!isInt) {
                resArr[scoreInt] = rHalf;
            }
        }
        start_1.setImageResource(resArr[0]);
        start_2.setImageResource(resArr[1]);
        start_3.setImageResource(resArr[2]);
        start_4.setImageResource(resArr[3]);
        start_5.setImageResource(resArr[4]);
        scoreText.setText(String.valueOf(score));
        if (displayDigit) {
            scoreText.setVisibility(View.VISIBLE);
        } else {
            scoreText.setVisibility(View.GONE);
        }
    }

    public void setScoreWithFocus(int score, boolean displayDigit) throws Exception {

        if (score > 5) {
            throw new Exception("score to large");
        }

        int[] resArr = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i < score) {
                resArr[i] = rFull;
            } else {
                resArr[i] = rEmpty;
            }
        }
        if (score < 5) {
            resArr[score] = rFocus;
        }
        start_1.setImageResource(resArr[0]);
        start_2.setImageResource(resArr[1]);
        start_3.setImageResource(resArr[2]);
        start_4.setImageResource(resArr[3]);
        start_5.setImageResource(resArr[4]);
        scoreText.setText(String.valueOf(score));
        if (displayDigit) {
            scoreText.setVisibility(View.VISIBLE);
        } else {
            scoreText.setVisibility(View.GONE);
        }
    }

    public void setFocus(int score, boolean isFocus) {
        if (score > 5) {
            return;
        }
        if (isFocus) {
            getIndexImg(score).setImageResource(rFocus);
        } else {
            getIndexImg(score).setImageResource(rEmpty);
        }
    }

    private ImageView getIndexImg(int score) {
        switch (score) {
            case 1:
                return start_1;
            case 2:
                return start_2;
            case 3:
                return start_3;
            case 4:
                return start_4;
            case 5:
                return start_5;
        }
        return null;
    }

}
