package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.activity.MessageCenterActivity;

import com.winhearts.arappmarket.activity.SearchActivity;
import com.winhearts.arappmarket.activity.SettingActivity;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.utils.Pref;

import java.util.ArrayList;

/**
 * 顶部标题
 *
 * @author huyf
 */
public class CommonTitleBar extends RelativeLayout implements View.OnFocusChangeListener {
    private String TAG = "CommonTitleBar";
    private Context mContext;
    private ImageView mSearchView;
    private ImageView mPersonCenterView;
    private MessageCenterView mMessageCenterView;
    private View mAlphaLayer;
    private String showMessage;
    private TitleBarDownListener titleBarDownListener;
    private View showHintView = null;
    private int x = 60;
    private int y = 13;
    private int resId;


    public CommonTitleBar(Context context) {
        super(context);

        init();

    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);


        init();
    }

    private void init() {
        this.mContext = getContext();
        // 初始UI
        initView();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (titleBarDownListener.onDown()) {
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && mMessageCenterView.getVisibility() == VISIBLE) {
                mMessageCenterView.showNextPreviewContent();
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                View findFocusView = findFocus();
                if (mPersonCenterView.equals(findFocusView) || mSearchView.equals(findFocusView)
                        || mMessageCenterView.equals(findFocusView)) {
                    mAlphaLayer.setVisibility(View.GONE);
                    if (titleBarDownListener.onDown()) {
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView() {

        LayoutInflater flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        flater.inflate(R.layout.common_title_bar, this, true);
        mSearchView = (ImageView) findViewById(R.id.iv_main_search);
        mPersonCenterView = (ImageView) findViewById(R.id.iv_main_person_center);
        mMessageCenterView = (MessageCenterView) findViewById(R.id.main_message_center);
        mSearchView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            }
        });

        mSearchView.setOnFocusChangeListener(this);
        mPersonCenterView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mContext, SettingActivity.class);
                mContext.startActivity(intent);
            }
        });
        mPersonCenterView.setOnFocusChangeListener(this);
        mMessageCenterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //待补上UploadFuncUsedLogic
                Intent intent = new Intent(mContext, MessageCenterActivity.class);
                mContext.startActivity(intent);
            }
        });
        mMessageCenterView.setOnFocusChangeListener(this);
        mAlphaLayer = findViewById(R.id.iv_main_alpha_layer);
        String showMessageCenter = Pref.getString(Pref.SHOW_MESSAGE_CENTER, getContext(), "1");
        if (showMessageCenter.equals("1")) {
            mPersonCenterView.setNextFocusLeftId(R.id.main_message_center);
            mMessageCenterView.setFocusable(true);
            mMessageCenterView.setVisibility(VISIBLE);
        } else {
            mMessageCenterView.setVisibility(INVISIBLE);
            mMessageCenterView.setFocusable(false);
            mPersonCenterView.setNextFocusLeftId(R.id.iv_main_person_center);

        }
    }

    private MainScoreHintPopup mainScoreHintPopup;


    public void showDownloadNum(View view, boolean isFirst) {
        if (showHintView != null && showHintView != view) {
            return;
        }
        x = 50;
        y = 8;
        showHintView = view;
        resId = R.drawable.popup_download_hint_bg;
        destroyPopWin();
        ArrayList<DownRecordInfo> loadingList = DownloadManager.getDownLoadRecord(DownloadManager.LOADING, getContext());
        ArrayList<DownRecordInfo> waitList = DownloadManager.getDownLoadRecord(DownloadManager.WAIT, getContext());
        ArrayList<DownRecordInfo> createList = DownloadManager.getDownLoadRecord(DownloadManager.CREATE_TASK, getContext());
        loadingList.addAll(waitList);
        loadingList.addAll(createList);
        if (loadingList.size() > 0) {
            showMessage = "正在下载" + loadingList.size() + "个应用";
            showHintView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public void destroyPopWin() {
        if (mainScoreHintPopup != null) {
            mainScoreHintPopup.destroy();
        }

    }

    /**
     * 新消息的阅读状态，是否已读
     */
    public void queryMessageReadState() {
        mMessageCenterView.updateViewVisitity();
    }


    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (showHintView != null) {
                mainScoreHintPopup = new MainScoreHintPopup((Activity) mContext);
                mainScoreHintPopup.show(showHintView, x, y, resId, showMessage);
                showHintView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
                showHintView = null;
            }
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            MainActivity.oldMenuId = null;
            MainActivity.oldSubMenuId = null;
            mAlphaLayer.setVisibility(View.VISIBLE);
            mMessageCenterView.setMessagePreviewVisiable(View.VISIBLE);
        } else {
            View view = findFocus();
            if (view != mSearchView && view != mPersonCenterView && view != mMessageCenterView) {
                mAlphaLayer.setVisibility(View.GONE);
            }
            //当标题栏的icon都失去焦点时即标题栏失去焦点时隐藏预览的textview
            if (!mSearchView.isFocused() && !mPersonCenterView.isFocused() && !mMessageCenterView.isFocused()) {
                mMessageCenterView.setMessagePreviewVisiable(View.GONE);
            }
        }
    }

    public void setTitleBarDownListener(TitleBarDownListener t) {
        this.titleBarDownListener = t;
    }

    public interface TitleBarDownListener {
        boolean onDown();
    }

    public void requestDefaultItem() {
        if (mSearchView != null) {
            mSearchView.requestFocus();
        }
    }
}
