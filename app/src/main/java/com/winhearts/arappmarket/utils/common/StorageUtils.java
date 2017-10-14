package com.winhearts.arappmarket.utils.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 存储位置
 *
 * @author liw
 */
public class StorageUtils {
    private static final String TAG = "StorageUtils";

    /* Checks if external storage is available for read and write */
    static public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        //用于有些盒子判断有SD卡，但是无法写入的问题
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + "testCreates");
            return destDir.exists() || destDir.mkdirs();
        }
        return false;
    }

    /**
     * 获取/data/data/包名/file   的权限
     *
     * @return file
     */
    public static File getFilesAuthority(String fileName) {
        File file = new File(VpnStoreApplication.getApp().getApplicationContext().getFilesDir(), fileName);
        String command = "chmod 777 " + file.getAbsolutePath();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取文件路径
     *
     * @param fileName
     * @return
     */
    public static File getPrivateFile(String fileName) {
        return new File(VpnStoreApplication.getApp().getApplicationContext().getFilesDir(), fileName);
    }

    /* Checks if external storage is available to at least read */
    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    // /data/data 下私有目录
    static public File getSelfDir(Context context, String dirName) {
        return context.getDir(dirName, Context.MODE_PRIVATE);
    }

    /**
     * 获取公用目录DownLoad
     *
     * @param
     * @return
     */
    static public File getPublicDir() {

        if (isExternalStorageWritable()) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            LogDebugUtil.d(TAG, file.getPath());
            return file;
        } else {
            return null;
        }

    }

    /**
     * 私有目录
     *
     * @return
     */
    static public File getPrivateDir(Context context) {

        if (isExternalStorageWritable()) {

            return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        } else {
            return null;
        }

    }

    static public String getSdcard(String path, String name) {
        if (isExternalStorageWritable()) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            LogDebugUtil.d(TAG, destDir.getPath() + File.separator + name);
            return destDir.getPath() + File.separator + name;
        } else {
            return null;
        }
    }

    static public File getDir(String path) {
        if (isExternalStorageWritable()) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            return destDir;
        } else {
            return null;
        }
    }


    static public File getSdcardSrcFile(String path, String name) {
        if (isExternalStorageWritable()) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            LogDebugUtil.d(TAG, destDir.getPath() + File.separator + name);
            String dstPath = destDir.getPath() + File.separator + name;
            File file = new File(dstPath);

            return file;
        } else {
            return null;
        }

    }

    static public File getSdcardFile(String path, String name, boolean isDelete) {
        if (isExternalStorageWritable()) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            LogDebugUtil.d(TAG, destDir.getPath() + File.separator + name);
            String dstPath = destDir.getPath() + File.separator + name;
            File file = new File(dstPath);

            try {
                if (file.exists() && isDelete) {
                    //删除原有下载的文件
                    FileUtils.forceDelete(file);
                    file.createNewFile();
                } else if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LoggerUtil.d(TAG, e.toString());
            }

            return file;
        } else {
            return null;
        }

    }


    static public File getSdcardNewFile(String path, String name) {
        if (isExternalStorageWritable()) {
            File destDir = new File(Environment.getExternalStorageDirectory() + File.separator + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            LogDebugUtil.d(TAG, destDir.getPath() + File.separator + name);
            String dstPath = destDir.getPath() + File.separator + name;
            File file = new File(dstPath);


            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    LoggerUtil.d(TAG, e.toString());
                }
            } else {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    LoggerUtil.d(TAG, e.toString());
                }
            }
            return file;
        } else {
            return null;
        }
    }

    public static String getFreeMemory(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return Formatter.formatFileSize(context, availableBlocks * blockSize);
        } else {
            return "-1";
        }
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取用户外部数据存储目录 (Android/data/<package-name>/files/)
     *
     * @return
     */
    public static File getUserDataDir() {
        if (isExternalStorageWritable()) {
            Context context = VpnStoreApplication.getApp().getApplicationContext();
            File file = context.getExternalFilesDir(null);
            if (file != null) {
                if (!file.exists()) {
                    file.mkdirs();
                }
//            LogDebugUtil.d(TAG, "---userDataPath---" + file.getAbsolutePath());
                return file;
            }
        }
        return null;
    }
}
