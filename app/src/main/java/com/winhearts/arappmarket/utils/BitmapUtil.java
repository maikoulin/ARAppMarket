package com.winhearts.arappmarket.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;

import com.blurry.process.NativeBlurProcess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 位图处理工具类
 *
 * @author
 */
public class BitmapUtil {
    /**
     * 获取sdcard图片，并对图片进行压缩
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap getBitmapFromFile(String path, int width, int height) {

        try {
            if (width <= 0 || height <= 0) {
                long length;// 图片字节大小
                File file = new File(path);
                if (!file.exists()) {
                    return null;
                }
                length = file.length();
                // 解码位图
                return BitmapFactory.decodeFile(path, bitmapOtions(inSampleSize(length)));
            } else {
                return BitmapFactory.decodeFile(path, bitmapOtions(path, width, height));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.gc();
            System.out.println("使用getBitmapFromFile()方法内存溢出带宽高！！！");
        }
        return null;
    }

    /**
     * 把文件输入流转为图片
     *
     * @param fileInput
     * @return
     */
    public static Bitmap decodeFileDescriptor(FileInputStream fileInput, int width, int height) {
        try {
            FileDescriptor fd = fileInput.getFD();
            if (width <= 0 || height <= 0) {
                int size = inSampleSize(fileInput.available());
                return BitmapFactory.decodeFileDescriptor(fd, null, bitmapOtions(size));
            } else {
                return BitmapFactory.decodeFileDescriptor(fd, null, bitmapOtions(fileInput, width, height));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            System.out.println("使用decodeFileDescriptor()方法内存溢出");
        }
        return null;
    }

    /**
     * 把字节数组转为图片
     *
     * @param data 字节数组
     * @return
     */
    public static Bitmap decodeByteArray(byte[] data, int width, int height) {
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOtions(data, width, height));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            System.out.println("使用decodeByteArray()方法内存溢出");
        }
        return null;
    }

    /**
     * 把字节数组转为图片
     *
     * @param data 字节数组
     * @return
     */
    public static Bitmap decodeByteArray(byte[] data, int bitmapSize) {
        int size = inSampleSize(data.length, bitmapSize);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOtions(size));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            System.out.println("使用decodeByteArray()方法内存溢出");
        }
        return null;
    }

    // 图片设置
    private static BitmapFactory.Options bitmapOtions(int size) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 为位图设置200K的缓存
        opts.inTempStorage = new byte[200 * 1024];
        // Android默认的颜色模式为ARGB_8888
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        opts.inSampleSize = size;
        // 设置解码位图的尺寸信息
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        return opts;
    }

    /**
     * 根据显示的宽高设置图片缩放比例
     *
     * @param width
     * @param height
     * @return
     */
    private static BitmapFactory.Options bitmapOtions(String filePath, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 为位图设置200K的缓存
        opts.inTempStorage = new byte[200 * 1024];
        // Android默认的颜色模式为ARGB_8888
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        return bitmapOtionsDEC(opts, width, height);
    }

    //
    private static BitmapFactory.Options bitmapOtions(byte[] data, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 为位图设置200K的缓存
        opts.inTempStorage = new byte[200 * 1024];
        // Android默认的颜色模式为ARGB_8888
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        return bitmapOtionsDEC(opts, width, height);
    }

    //
    private static BitmapFactory.Options bitmapOtions(FileInputStream filePath, int width, int height) throws IOException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 为位图设置200K的缓存
        opts.inTempStorage = new byte[200 * 1024];
        // Android默认的颜色模式为ARGB_8888
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(filePath.getFD(), null, opts);

        return bitmapOtionsDEC(opts, width, height);
    }

    private static BitmapFactory.Options bitmapOtionsDEC(BitmapFactory.Options opts, int width, int height) {
        // 设置位图缩放比例 width，hight设为原来的四分一（该参数请使用2的整数倍）
        int inSampleSize = 1;
        int outHeight = opts.outHeight;
        int outWidth = opts.outWidth;
        if (outWidth > width || outHeight > height) {
            int rWidth = Math.round((float) outWidth / (float) width);
            int rHeight = Math.round((float) outHeight / (float) height);
            inSampleSize = rWidth < rHeight ? rWidth : rHeight;
        }
        opts.inSampleSize = inSampleSize;
        // 设置解码位图的尺寸信息
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        return opts;
    }

    // 根据内存计算压缩比例 @param picSize 单位字节
    private static int inSampleSize(long picSize) {
        long sizeKb = picSize / 1024;
        int size = (int) (sizeKb / getCompress());
        if (size == 0 || size == 1) {
            size = 1;
        } else if (size % 2 != 0) {
            size = size - 1;
        }
        return size;
    }

    // 压缩比例，劲量的使压缩出来的图片接近size
    private static int inSampleSize(long picSize, int maxSize) {
        long sizeKb = picSize / 1024;
        int size = (int) (sizeKb / maxSize);
        if (size == 0 || size == 1) {
            size = 1;
        } else if (size % 2 != 0) {
            size = size - 1;
        }
        return size;
    }

    // 根据应用程序最大可用内存获取图片压缩较优大小
    public static long getCompress() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return maxMemory / (1024 * 1100);
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }


    //高斯模糊
    public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);

        Type t = allocation.getType();

        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius);
        //Set input for script
        blurScript.setInput(allocation);
        //Call script for output allocation
        blurScript.forEach(blurredAllocation);

        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);

        //Destroy everything to free memory
        allocation.destroy();
        blurredAllocation.destroy();
        blurScript.destroy();
        t.destroy();
        rs.destroy();
        return bitmap;
    }

    public static Bitmap blurNative(Bitmap bitmap, float radius, Context context) {

        //Create renderscript
        RenderScript rs = RenderScript.create(context);
        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);
        Type t = allocation.getType();
        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        NativeBlurProcess blurProcess = new NativeBlurProcess();
        blurredAllocation.copyFrom(blurProcess.blur(bitmap, radius));
        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);
        allocation.destroy();
        blurredAllocation.destroy();
        t.destroy();
        rs.destroy();
        return bitmap;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
