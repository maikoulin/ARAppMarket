package com.winhearts.arappmarket.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.AdvertisementEntity;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

/**
 * 名称：AdvertisementActivity
 * 描述：广告弹窗页
 *
 * @author lmh
 */
public class AdvertisementActivity extends BaseActivity {
    private final static String TAG = "adActivity";
    private SimpleDraweeView adImage;
    private TextView adTime;
    private Intent intent;
    private ImageView adBottom;
    private AdvertisementEntity advertisementEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub


        super.onCreate(savedInstanceState);
        LoggerUtil.d(TAG, "-------onCreate----");
        intent = getIntent();
        advertisementEntity = (AdvertisementEntity) intent.getSerializableExtra("advertisement");
        //把弹过的广告Id保存起来
        PrefNormalUtils.putString(this, PrefNormalUtils.AD_IMAGE_HOME, advertisementEntity.getList().get(0).getId());
        setContentView(R.layout.activity_advertisement);
        adImage = (SimpleDraweeView) this.findViewById(R.id.iv_ad_image);
        adTime = (TextView) this.findViewById(R.id.tv_ad_time);
        adBottom = (ImageView) this.findViewById(R.id.iv_ad_bottom);
        getImage();
        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = advertisementEntity.getList().get(0).getType();
                Intent intent = new Intent();
                if (type.equals("NONE")) {
                    LoggerUtil.d(TAG, "-----AdvertisementActivity----NONE----");
                    intent.setClass(AdvertisementActivity.this, MainActivity.class);
                    startActivity(intent);
                    timer.cancel();
                    finish();
                } else if (type.equals("SOFTWARE")) {
                    LoggerUtil.d(TAG, "-----AdvertisementActivity----SOFTWARE----");
                    LoggerUtil.d(TAG, advertisementEntity.getList().get(0).toString());
                    intent.setClass(AdvertisementActivity.this, AppDetailActivity.class);
                    intent.putExtra("packageName", advertisementEntity.getList().get(0).getSoftware().getPackageName());
                    intent.putExtra("isBackMain", true);
                    startActivity(intent);
                    timer.cancel();
                    finish();
                } else if (type.equals("TOPIC")) {
                    LoggerUtil.d(TAG, "-----AdvertisementActivity----TOPIC----");
                    intent.setClass(AdvertisementActivity.this, TopicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("topic", advertisementEntity.getList().get(0).getTopic());
                    intent.putExtras(bundle);
                    intent.putExtra("isBackMain", true);
                    startActivity(intent);
                    timer.cancel();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    //倒计时计时器
    CountDownTimer timer = new CountDownTimer(10000, 1000) {
        @Override

        public void onTick(long millisUntilFinished) {
            adTime.setText("" + millisUntilFinished / 1000 + "s");
        }

        @Override

        public void onFinish() {
            finish();
        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            timer.cancel();
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    //加载广告图片
    private void getImage() {

        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                adTime.setVisibility(View.VISIBLE);
                adBottom.setVisibility(View.VISIBLE);
                timer.start();
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                finish();
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setOldController(adImage.getController())
                .setUri(Uri.parse(advertisementEntity.getList().get(0).getImageUrl()))
                .build();
        adImage.setController(controller);
    }
}
