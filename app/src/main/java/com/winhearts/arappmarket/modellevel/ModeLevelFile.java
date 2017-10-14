package com.winhearts.arappmarket.modellevel;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.winhearts.arappmarket.download.loader.DownloadUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Description:与引导页图片相关
 * Created by lmh on 2016/4/12.
 */
public class ModeLevelFile {


    static public File getLoadImageFile(String layoutCode) {


        String url = PrefNormalUtils.getString(layoutCode + "_" + PrefNormalUtils.URL_START_BKG_IMG);
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return StorageUtils.getPrivateFile(layoutCode + "_" + url.hashCode());
    }

    static public File getBgImageFile(String layoutCode) {
        String url = PrefNormalUtils.getString(layoutCode + "_" + PrefNormalUtils.URL_BG_IMG);
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return StorageUtils.getPrivateFile(layoutCode + "_" + url.hashCode());
    }


    static public void syncBgImageFile(String layoutCode) {
        String key = layoutCode + "_" + PrefNormalUtils.URL_BG_IMG;
        String oldUrl = PrefNormalUtils.getString(key);
        String newUrl = PrefNormalUtils.getString("new_" + key);
        if (!TextUtils.isEmpty(newUrl) && !newUrl.equals(oldUrl)) {
            PrefNormalUtils.putString(key, newUrl);
            PrefNormalUtils.remove("new_" + key);
            PrefNormalUtils.putBoolean(PrefNormalUtils.URL_BG_IMG_IS_CHANGE, true);
            if (!TextUtils.isEmpty(oldUrl)) {
                try {
                    File oldFile = StorageUtils.getPrivateFile(layoutCode + "_" + oldUrl.hashCode());
                    FileUtils.forceDelete(oldFile);
                } catch (IOException e) {
                    LogDebugUtil.i("forceDelete", e.getMessage());
                }
            }
        }
    }

    static public void saveBgImageFile(String layoutCode, String url) {

        if (TextUtils.isEmpty(url)) {
            deleteOldFile(layoutCode + "_" + PrefNormalUtils.URL_BG_IMG, layoutCode);
            return;
        }

        String fileUrl = PrefNormalUtils.getString(layoutCode + "_" + PrefNormalUtils.URL_BG_IMG);
        if (!TextUtils.isEmpty(fileUrl) && fileUrl.equals(url)) {
            return;
        }
        saveUrl2file(layoutCode + "_" + PrefNormalUtils.URL_BG_IMG, url, layoutCode, false);
    }

    static public void saveLoadImageFile(String layoutCode, String url) {

        if (TextUtils.isEmpty(url)) {
            deleteOldFile(layoutCode + "_" + PrefNormalUtils.URL_START_BKG_IMG, layoutCode);
            return;
        }
        String fileUrl = PrefNormalUtils.getString(layoutCode + "_" + PrefNormalUtils.URL_START_BKG_IMG);
        if (!TextUtils.isEmpty(fileUrl) && fileUrl.equals(url)) {
            return;
        }

        saveUrl2file(layoutCode + "_" + PrefNormalUtils.URL_START_BKG_IMG, url, layoutCode, true);
    }


    public static void deleteOldFile(String key, String layoutCode) {
        String oldUrl = PrefNormalUtils.getString(key);
        if (!TextUtils.isEmpty(oldUrl)) {
            File oldFile = StorageUtils.getPrivateFile(layoutCode + "_" + oldUrl.hashCode());
            Fresco.getImagePipeline().evictFromCache(Uri.fromFile(oldFile));
            Fresco.getImagePipeline().evictFromDiskCache(Uri.fromFile(oldFile));
            Fresco.getImagePipeline().evictFromMemoryCache(Uri.fromFile(oldFile));
            PrefNormalUtils.remove(key);
            try {
                FileUtils.forceDelete(oldFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveUrl2file(final String key, final String newUrl, final String layoutCode, final boolean isErase) {

        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                try {
                    URL url = new URL(newUrl);
                    //1、下载
                    File tmpFile = StorageUtils.getPrivateFile("tmp_" + layoutCode + "_" + newUrl.hashCode());
                    FileUtils.copyURLToFile(url, tmpFile);

                    int size = new DownloadUtil().getFileLength(newUrl);
                    File file = StorageUtils.getPrivateFile(layoutCode + "_" + newUrl.hashCode());
                    //2、保存
                    if (tmpFile.length() == size) {
                        FileUtils.copyFile(tmpFile, file);
                        FileUtils.forceDelete(tmpFile);//
                    } else {
                        subscriber.onError(new Exception("download error size no same url = " + newUrl));
                        return;
                    }
                    subscriber.onNext(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new Exception("download error url = " + newUrl));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File file) {
                        final String oldUrl = PrefNormalUtils.getString(key);
                        if (!TextUtils.isEmpty(oldUrl)) {
                            File oldFile = StorageUtils.getPrivateFile(layoutCode + "_" + oldUrl.hashCode());
                            //清理图片缓存中的缓存
                            if (isErase) {
                                Fresco.getImagePipeline().evictFromCache(Uri.fromFile(oldFile));
                                Fresco.getImagePipeline().evictFromDiskCache(Uri.fromFile(oldFile));
                                Fresco.getImagePipeline().evictFromMemoryCache(Uri.fromFile(oldFile));
                                PrefNormalUtils.putString(key, newUrl);
                                try {
                                    FileUtils.forceDelete(oldFile);//应该保证新旧 url 完全不一样
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                PrefNormalUtils.putString("new_" + key, newUrl);
                            }
                        } else {
                            //保存新的文件 tag 覆盖旧的
                            if (isErase) {
                                PrefNormalUtils.putString(key, newUrl);
                            } else {
                                PrefNormalUtils.putString("new_" + key, newUrl);
                            }
                        }

                    }
                });


    }


}
