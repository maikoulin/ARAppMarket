package com.winhearts.arappmarket.constant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.modellevel.ModeLevelFile;
import com.winhearts.arappmarket.utils.ScreenUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description:公用的Hierarchy
 * Created by lmh on 2016/3/24.
 */
public class CommonHierarchy {
    private BitmapDrawable loadBitmapDrawable = null;

    static private final String TAG = "CommonHierarchy";

//    private  static String urlBg = null;

    /**
     * 以图表icon 为默认背景的小图表
     *
     * @param simpleDraweeView
     * @return
     */
    static public GenericDraweeHierarchy setHierarchyAppIcon(SimpleDraweeView simpleDraweeView) {
        GenericDraweeHierarchy hierarchy = null;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (simpleDraweeView.hasHierarchy()) {
            hierarchy = simpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            simpleDraweeView.setHierarchy(hierarchy);
        }
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        hierarchy.setPlaceholderImage(R.drawable.background_app_icon);
        return hierarchy;
    }

    public static void showThumb(Context context, Uri uri, SimpleDraweeView draweeView, int width, int height) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtil.dip2px(context, width), ScreenUtil.dip2px(context, height)))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)
//                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }

    public static void showThumb(Context context, Uri uri, SimpleDraweeView draweeView) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtil.dip2px(context, 120), ScreenUtil.dip2px(context, 120)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)
//                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }

    public static void showAppIcon(Context context, Uri uri, SimpleDraweeView draweeView) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtil.dip2px(context, 40), ScreenUtil.dip2px(context, 40)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
//                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }

    public static void showAppIconSize(Context context, Uri uri, SimpleDraweeView draweeView, int size) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtil.dip2px(context, size), ScreenUtil.dip2px(context, 40)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
//                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }

    /**
     * 以图表icon 为默认背景的小图表
     *
     * @param simpleDraweeView
     * @return
     */
    static public GenericDraweeHierarchy setHierarchyCardPic(SimpleDraweeView simpleDraweeView) {
        GenericDraweeHierarchy hierarchy = null;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (simpleDraweeView.hasHierarchy()) {
            hierarchy = simpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            simpleDraweeView.setHierarchy(hierarchy);
        }
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        hierarchy.setPlaceholderImage(R.drawable.comm_card_default);
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        if (roundingParams == null) {
            roundingParams = new RoundingParams();
        }
        roundingParams.setCornersRadius(5);
        hierarchy.setRoundingParams(roundingParams);
        return hierarchy;
    }

    /**
     * 设置simpleDraweeView站位图
     *
     * @param simpleDraweeView
     * @param resourceId
     * @return
     */
    static public GenericDraweeHierarchy setHierarchyDrawable(SimpleDraweeView simpleDraweeView, int resourceId) {
        GenericDraweeHierarchy hierarchy = null;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (simpleDraweeView.hasHierarchy()) {
            hierarchy = simpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            simpleDraweeView.setHierarchy(hierarchy);
        }

        hierarchy.setPlaceholderImage(resourceId);
        hierarchy.setFailureImage(VpnStoreApplication.app.getResources().getDrawable(R.drawable.background));
        return hierarchy;
    }

    /**
     * 单独设置圆角方法。
     *
     * @param simpleDraweeView
     * @param radius
     */
    static private void setRoundedCornerRadius(SimpleDraweeView simpleDraweeView, float radius) {
        RoundingParams roundingParams =
                simpleDraweeView.getHierarchy().getRoundingParams();
        if (roundingParams == null) {
            roundingParams = new RoundingParams();
        }
//        roundingParams.setBorder(R.color.red, radius);
//        roundingParams.setRoundAsCircle(true);
        roundingParams.setCornersRadius(radius);

        simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);
    }

    /**
     * 将文件生成位图
     *
     * @param path
     * @return
     * @throws IOException
     */
    public BitmapDrawable getImageDrawable(String path)
            throws IOException {
        //打开文件
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] bt = new byte[512];

        //得到文件的输入流
        InputStream in = new FileInputStream(file);

        //将文件读出到输出流中
        int readLength = in.read(bt);
        while (readLength != -1) {
            outStream.write(bt, 0, readLength);
            readLength = in.read(bt);
        }

        //转换成byte 后 再格式化成位图
        byte[] data = outStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// 生成位图
        BitmapDrawable bd = new BitmapDrawable(bitmap);

        return bd;
    }

    public BitmapDrawable getImageDrawable(File file)
            throws IOException {
        //打开文件
        if (!file.exists()) {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] bt = new byte[512];

        //得到文件的输入流
        InputStream in = new FileInputStream(file);

        //将文件读出到输出流中
        int readLength = in.read(bt);
        while (readLength != -1) {
            outStream.write(bt, 0, readLength);
            readLength = in.read(bt);
        }

        //转换成byte 后 再格式化成位图
        byte[] data = outStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// 生成位图
        BitmapDrawable bd = new BitmapDrawable(bitmap);

        return bd;
    }

    public void destroyDrawable() {
        if (loadBitmapDrawable != null && loadBitmapDrawable.getBitmap() != null) {
            loadBitmapDrawable.getBitmap().recycle();
        }
    }

    /**
     * 加载页背景图
     *
     * @param simpleDraweeView
     */
    static public void setLoadImage(SimpleDraweeView simpleDraweeView, String layoutCode) {
        GenericDraweeHierarchy hierarchy = null;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (simpleDraweeView.hasHierarchy()) {
            hierarchy = simpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            simpleDraweeView.setHierarchy(hierarchy);
        }
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        File file = ModeLevelFile.getLoadImageFile(layoutCode);
        if (file != null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            simpleDraweeView.setImageURI(uri);
        } else {
            simpleDraweeView.setImageURI(Uri.parse("res://drawable/" + R.drawable.load_bg));
        }
    }

    /**
     * 加载页背景图
     *
     * @param simpleDraweeView
     */
    static public void setBgImage(SimpleDraweeView simpleDraweeView) {
        GenericDraweeHierarchy hierarchy = null;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (simpleDraweeView.hasHierarchy()) {
            hierarchy = simpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            simpleDraweeView.setHierarchy(hierarchy);
        }

        hierarchy.setPlaceholderImage(R.drawable.background);
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
//        hierarchy.setFailureImage(VpnStoreApplication.app.getResources().getDrawable(R.drawable.background));


        File file = ModeLevelFile.getBgImageFile(VpnStoreApplication.getApp().getLayoutCode());
        if (file != null && file.exists()) {
            simpleDraweeView.setImageURI(Uri.fromFile(file));
        }
    }


}
