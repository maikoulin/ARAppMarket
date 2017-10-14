package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;

import java.util.ArrayList;

/**
 * 应用详情应用截图全屏浏览
 */
public class PicBrowseActivity extends BaseActivity implements OnTouchListener, GestureDetector.OnGestureListener {

    private ViewPager mViewPager;
    private GestureDetector gestureDetector;
    private String[] list;
    private int index;
    private LinearLayout framePages;
    private Context mcontext;
    // 填充ViewPager的数据适配器
    private PagerAdapter myAdapter;
    private ArrayList<SimpleDraweeView> listViews = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_browse);
        init();

    }

    private void init() {
        mcontext = this;
        framePages = (LinearLayout) findViewById(R.id.frame_pages);
        mViewPager = (ViewPager) findViewById(R.id.image_viewpager);
        gestureDetector = new GestureDetector(this);
        gestureDetector.setIsLongpressEnabled(true);
        list = getIntent().getStringArrayExtra("urlList");
        index = getIntent().getIntExtra("index", 0);

        int count = 0;
        for (String info : list) {
            SimpleDraweeView imagePage = new SimpleDraweeView(mcontext);
            LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lparams.leftMargin = 10;
            if (count == index) {
                imagePage.setBackgroundResource(R.drawable.page_now);
            } else {
                imagePage.setBackgroundResource(R.drawable.page);
            }
            framePages.addView(imagePage, lparams);
            count++;
        }

        String info;
        for (int i = 0; i < list.length + 2; i++) {
            SimpleDraweeView imageView = new SimpleDraweeView(mcontext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setOnTouchListener(this);
            listViews.add(imageView);
        }
        myAdapter = new PagerAdapter() {
            String info;

            @Override
            public int getCount() {
                return listViews.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                position = position % listViews.size();
                SimpleDraweeView view = listViews.get(position);
                ((ViewPager) container).removeView(view);
            }

            @Override
            public Object instantiateItem(View container, int position) {
                if (position == 0) {
                    info = list[list.length - 1];
                } else if (position == (listViews.size() - 1)) {
                    info = list[0];
                } else {
                    info = list[position - 1];
                }
                listViews.get(position).setImageURI(Uri.parse(info));
                ((ViewPager) container).addView(listViews.get(position));
                return listViews.get(position);
            }

        };
        mViewPager.setAdapter(myAdapter);
        mViewPager.setCurrentItem(index + 1);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                SimpleDraweeView imageView = (SimpleDraweeView) framePages.getChildAt(index);
                imageView.setBackgroundResource(R.drawable.page);
                int pageindex;
                if (position == 0) {
                    index = list.length - 1;
                    pageindex = list.length;
                } else if (position == listViews.size() - 1) {
                    index = 0;
                    pageindex = 1;
                } else {
                    index = position - 1;
                    pageindex = position;
                }
                imageView = (SimpleDraweeView) framePages.getChildAt(index);
                imageView.setBackgroundResource(R.drawable.page_now);
                if (position != pageindex) {
                    mViewPager.setCurrentItem(pageindex, false);
                    // return;
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
            }
        });
    }


    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        PicBrowseActivity.this.finish();
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        gestureDetector.onTouchEvent(event);
        return true;
    }


    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        // TODO Auto-generated method stub
        return false;
    }


}
