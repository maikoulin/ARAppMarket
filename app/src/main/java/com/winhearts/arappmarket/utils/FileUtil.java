package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.winhearts.arappmarket.model.LogInfo;
import com.winhearts.arappmarket.model.LogInfos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件工具类
 */
public class FileUtil {
    public static void clearFile(String filePath) {
        try {
            File f = new File(filePath);
            FileWriter fw = new FileWriter(f);
            fw.write("");
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String inputStream2String(InputStream is) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (int i; (i = is.read(buf)) != -1; ) {
            baos.write(buf, 0, i);
        }
        String data = baos.toString("UTF-8");
        baos.close();
        return data;
    }

	/*日志的格式为
     * "%d{yyyy:MM:dd HH:mm:ss},%p, %m%n(tag,content)"
	 * 日期 ， 级别，标题 ，内容 |
	 */

    public static LogInfos getLogInfos(Context mContext, String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            FileInputStream inputStream = null;
            if (file.exists()) {
                try {
                    inputStream = new FileInputStream(file);
                    if (inputStream.available() > 0) {
                        String logContent = inputStream2String(inputStream);
                        String[] logItems = logContent.split("\\|");

                        //用map去重复
                        Map<String, LogInfo> map = new ArrayMap<>();
                        for (int i = 0; i < logItems.length - 1; i++) {
                            String logItem = logItems[i];
                            String[] itemContent = logItem.split(",",5);
                            LogInfo logInfo = new LogInfo();
                            if (itemContent.length == 5) {
                                logInfo.setCreateTime(itemContent[3]);
                                logInfo.setLevel(itemContent[1]);
                                logInfo.setTitle(itemContent[2]);
                                logInfo.setContent(itemContent[4]);
                                map.put(itemContent[2], logInfo);
                            }
                        }

//					//map转list
                        List<LogInfo> tmpLogInfoList = new ArrayList<>();
                        for (Object o : map.keySet()) {
                            String key = o.toString();
                            tmpLogInfoList.add(map.get(key));
                        }


                        LogInfos logInfos = new LogInfos();
                        logInfos.setProdCode(mContext.getPackageName());
                        logInfos.setAppVer(Util.getVersionName(mContext));
                        logInfos.setClientCode(MacUtil.getMacAddress());
                        logInfos.setClientType(android.os.Build.MODEL);
                        logInfos.setClientOsVer("android:" + android.os.Build.VERSION.RELEASE);
                        logInfos.setLogList(tmpLogInfoList);
                        return logInfos;
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 序列化到本地文件存储, 默认覆盖文件内容
     *
     * @param path
     * @param obj
     */
    public static void saveObject(String path, Object obj) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        File file = new File(path);
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从本地文件反序列化内容到内存中
     *
     * @param path
     * @return
     */
    public static Object restoreObject(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        Object obj = null;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }


}
