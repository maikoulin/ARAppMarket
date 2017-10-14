package com.winhearts.arappmarket.utils;

import android.graphics.Bitmap;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;

/**
 * fresco图片后处理器
 * Created by lmh on 2016/5/12.
 */
public class MyBasePostprocessor extends BaseRepeatedPostProcessor {
//    @Override
//    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
////        BitmapUtil.doBlur(destBitmap, 5, true);
//        for (int x = 0; x < destBitmap.getWidth(); x += 6) {
//            for (int y = 0; y < destBitmap.getHeight(); y += 6) {
//                destBitmap.setPixel(x, y, Color.RED);
//            }
//        }
//    }

//    @Override
//    public void process(Bitmap bitmap) {
//        BitmapUtil.blurBitmap(bitmap, 25, VpnStoreApplication.getApp().getApplicationContext());
//
//    }

    @Override
    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                sourceBitmap.getWidth() / 2,
                sourceBitmap.getHeight() / 2);
        try {
            Bitmap destBitmap = bitmapRef.get();
            for (int x = 0; x < destBitmap.getWidth(); x++) {
                for (int y = 0; y < destBitmap.getHeight(); y++) {
                    destBitmap.setPixel(x, y, sourceBitmap.getPixel(2 * x, 2 * y));
                }
            }
            BitmapUtil.blurNative(destBitmap, 25, VpnStoreApplication.getApp().getApplicationContext());
//            BitmapUtil.blurBitmap(destBitmap, 25, VpnStoreApplication.getApp().getApplicationContext());
            return CloseableReference.cloneOrNull(bitmapRef);
        } finally {
            CloseableReference.closeSafely(bitmapRef);
        }
    }

    @Override
    public String getName() {
        return "redMeshPostprocessor";
    }

}