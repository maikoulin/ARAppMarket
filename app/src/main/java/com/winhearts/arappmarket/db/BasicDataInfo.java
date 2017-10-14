package com.winhearts.arappmarket.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.Columns.AppSourceColumns;
import com.winhearts.arappmarket.db.Columns.SmartFileDownlogColumns;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.SoftwareInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BasicDataInfo {
    /**
     * 获取每条线程已经下载的文件长度
     *
     * @param path
     * @return
     */
    public synchronized static Map<Integer, Integer> getDownData(SQLiteDatabase mSQLiteDatabase, String path) {
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_SMARTFILEDOWNLOG, null, SmartFileDownlogColumns.TABLE_DOWNPATH + "=?", new String[]{path}, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                data.put(cur.getInt(cur.getColumnIndex(SmartFileDownlogColumns.TABLE_THREADID)), cur.getInt(cur.getColumnIndex(SmartFileDownlogColumns.TABLE_DOWNLENGTH)));
            } while (cur.moveToNext());
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public synchronized static void saveDownLenght(SQLiteDatabase mSQLiteDatabase, String path, Map<Integer, Integer> map) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            ContentValues cv = new ContentValues();
            cv.put(SmartFileDownlogColumns.TABLE_DOWNPATH, path);
            cv.put(SmartFileDownlogColumns.TABLE_THREADID, entry.getKey());
            cv.put(SmartFileDownlogColumns.TABLE_DOWNLENGTH, entry.getValue());
            Database.insert(mSQLiteDatabase, DatabaseAccessor.TABLE_SMARTFILEDOWNLOG, cv);
        }
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public synchronized static void updateDownLenght(SQLiteDatabase mSQLiteDatabase, String path, Map<Integer, Integer> map) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            String selection = SmartFileDownlogColumns.TABLE_DOWNPATH + " =?" + " and " + SmartFileDownlogColumns.TABLE_THREADID + " = " + entry.getKey();
            ContentValues cv = new ContentValues();
            cv.put(SmartFileDownlogColumns.TABLE_DOWNPATH, path);
            cv.put(SmartFileDownlogColumns.TABLE_THREADID, entry.getKey());
            cv.put(SmartFileDownlogColumns.TABLE_DOWNLENGTH, entry.getValue());
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_SMARTFILEDOWNLOG, cv, selection, new String[]{path});
        }
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     *
     * @param path
     */
    public synchronized static void deleteDownRecord(SQLiteDatabase mSQLiteDatabase, String path) {
        Database.delete(mSQLiteDatabase, DatabaseAccessor.TABLE_SMARTFILEDOWNLOG, SmartFileDownlogColumns.TABLE_DOWNPATH + " =?", new String[]{path});
    }

    /**
     * 删除所有下载记录
     */
    public synchronized static void deleteAllDownRecord(SQLiteDatabase mSQLiteDatabase) {
        Database.delete(mSQLiteDatabase, DatabaseAccessor.TABLE_SMARTFILEDOWNLOG, null, null);
        Database.delete(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, null, null);
    }

    public synchronized static void addDownRecord(int downlength, int fileSize, String path, String appName, String iconUrl, String fileName, int state, String packageName, String appVersion) {
        SQLiteDatabase sQLiteDatabase = VpnStoreApplication.app.getSQLDatabase();
        addDownRecord(sQLiteDatabase, downlength, fileSize, path, appName, iconUrl, fileName, state, packageName, appVersion);

    }

    /**
     * 添加下载记录到下载列表
     */
    public synchronized static void addDownRecord(SQLiteDatabase mSQLiteDatabase, int downlength, int fileSize, String path, String appName, String iconUrl, String fileName, int state, String packageName, String appVersion) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_DOWNLOADURL + " =? ", new String[]{path}, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
//			cur.close();
//			return;
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_APPNAME, appName);
            cv.put(AppSourceColumns.TABLE_ICONURL, iconUrl);
//			cv.put(AppSourceColumns.TABLE_SOURCEID, sourceId);
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_DOWNLENGTH, downlength);
            cv.put(AppSourceColumns.TABLE_FILESIZE, fileSize);
            cv.put(AppSourceColumns.TABLE_FILENAME, fileName);
            cv.put(AppSourceColumns.TABLE_STATE, state);
            cv.put(AppSourceColumns.TABLE_PACKAGENAME, packageName);
            cv.put(AppSourceColumns.TABLE_APPVERSION, appVersion);
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, AppSourceColumns.TABLE_PACKAGENAME + " =? ", new String[]{packageName});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_APPNAME, appName);
            cv.put(AppSourceColumns.TABLE_ICONURL, iconUrl);
//			cv.put(AppSourceColumns.TABLE_SOURCEID, sourceId);
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_DOWNLENGTH, downlength);
            cv.put(AppSourceColumns.TABLE_FILESIZE, fileSize);
            cv.put(AppSourceColumns.TABLE_FILENAME, fileName);
            cv.put(AppSourceColumns.TABLE_STATE, state);
            cv.put(AppSourceColumns.TABLE_PACKAGENAME, packageName);
            cv.put(AppSourceColumns.TABLE_APPVERSION, appVersion);
            Database.insert(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv);
        }
        if (cur != null) {
            cur.close();
        }

    }

    /**
     * 更新下载记录到下载列表
     *
     * @param path
     */
    public synchronized static void updateDownRecord(SQLiteDatabase mSQLiteDatabase, int downlength, int fileSize, String path, String fileName) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_DOWNLOADURL + " =?", new String[]{path}, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_DOWNLENGTH, downlength);
            cv.put(AppSourceColumns.TABLE_FILESIZE, fileSize);
            cv.put(AppSourceColumns.TABLE_FILENAME, fileName);
            String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, selection, new String[]{path});
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    /**
     * 更新下载记录的状态
     *
     * @param path
     */
    public synchronized static void updateDownRecordState(SQLiteDatabase mSQLiteDatabase, String path, int state) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_DOWNLOADURL + " =?", new String[]{path}, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_STATE, state);
            String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, selection, new String[]{path});
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    /**
     * 跟新下载记录的状态
     */
    public synchronized static void updateStateBypackageName(SQLiteDatabase mSQLiteDatabase, String packageName, int state) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_PACKAGENAME + " =?", new String[]{packageName}, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_STATE, state);
            String selection = AppSourceColumns.TABLE_PACKAGENAME + " =?";
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, selection, new String[]{packageName});
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    /**
     * 跟新下载记录的包名
     *
     * @param path
     */
    public synchronized static void updateDownRecordpackageName(SQLiteDatabase mSQLiteDatabase, String path, String packageName) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_DOWNLOADURL + " =?", new String[]{path}, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_PACKAGENAME, packageName);
            String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, selection, new String[]{path});
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    /**
     * 跟新下载记录的版本号
     *
     * @param path
     */
    public synchronized static void updateDownRecordAppVersion(SQLiteDatabase mSQLiteDatabase, String path, String appVersion) {
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, AppSourceColumns.TABLE_DOWNLOADURL + " =?", new String[]{path}, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            ContentValues cv = new ContentValues();
            cv.put(AppSourceColumns.TABLE_DOWNLOADURL, path);
            cv.put(AppSourceColumns.TABLE_APPVERSION, appVersion);
            String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD, cv, selection, new String[]{path});
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    public synchronized static ArrayList<DownRecordInfo> getAllRecord() {
        SQLiteDatabase sQLiteDatabase = VpnStoreApplication.app.getSQLDatabase();
        return getAllRecord(sQLiteDatabase);
    }

    /**
     * 获取所有下载记录
     */
    public synchronized static ArrayList<DownRecordInfo> getAllRecord(SQLiteDatabase mSQLiteDatabase) {
        ArrayList<DownRecordInfo> list = new ArrayList<DownRecordInfo>();
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, null, null, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                DownRecordInfo info = new DownRecordInfo();
//				info.setSourceId(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_SOURCEID)));
                info.setDownlength(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLENGTH)));
                info.setFileSize(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_FILESIZE)));
                info.setAppName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPNAME)));
                info.setIconUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_ICONURL)));
                info.setDownloadUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLOADURL)));
                info.setFileName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_FILENAME)));
                info.setPackageName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_PACKAGENAME)));
                info.setAppVersion(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPVERSION)));
                info.setState(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_STATE)));
                list.add(info);
            } while (cur.moveToNext());
        }
        if (cur != null) {
            cur.close();
        }
        return list;
    }

    /**
     * 获取下载记录
     */
    public synchronized static DownRecordInfo getRecord(SQLiteDatabase mSQLiteDatabase, String path) {
        DownRecordInfo info = new DownRecordInfo();
        String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, selection, new String[]{path}, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
//				info.setSourceId(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_SOURCEID)));
            info.setDownlength(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLENGTH)));
            info.setFileSize(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_FILESIZE)));
            info.setAppName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPNAME)));
            info.setIconUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_ICONURL)));
            info.setDownloadUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLOADURL)));
            info.setFileName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_FILENAME)));
            info.setPackageName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_PACKAGENAME)));
            info.setAppVersion(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPVERSION)));
            info.setState(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_STATE)));
        } else {
            info = null;
        }
        if (cur != null) {
            cur.close();
        }
        return info;
    }
    /**
     * 获取下载记录
     */
    public synchronized static DownRecordInfo getRecordBypackageName(SQLiteDatabase mSQLiteDatabase, String packageName) {
        DownRecordInfo info = new DownRecordInfo();
        String selection = AppSourceColumns.TABLE_PACKAGENAME + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, selection, new String[]{packageName}, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
//				info.setSourceId(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_SOURCEID)));
            info.setDownlength(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLENGTH)));
            info.setFileSize(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_FILESIZE)));
            info.setAppName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPNAME)));
            info.setIconUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_ICONURL)));
            info.setDownloadUrl(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_DOWNLOADURL)));
            info.setFileName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_FILENAME)));
            info.setPackageName(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_PACKAGENAME)));
            info.setAppVersion(cur.getString(cur.getColumnIndex(AppSourceColumns.TABLE_APPVERSION)));
            info.setState(cur.getInt(cur.getColumnIndex(AppSourceColumns.TABLE_STATE)));
        } else {
            info = null;
        }
        if (cur != null) {
            cur.close();
        }
        return info;
    }

    /**
     * 删除记录
     */
    public synchronized static void DeleteRecord(SQLiteDatabase mSQLiteDatabase, String path) {
        Database.delete(mSQLiteDatabase, DatabaseAccessor.TABLE_DOWNRECORD,
                AppSourceColumns.TABLE_DOWNLOADURL + " =?", new String[]{path});

    }

    /**
     * 查询下载记录是否已经存在
     */
    public synchronized static boolean isExist(SQLiteDatabase mSQLiteDatabase, String path) {
        boolean ret = false;
        String selection = AppSourceColumns.TABLE_DOWNLOADURL + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, selection, new String[]{path}, null, null, null, null);
        ret = cur != null && cur.getCount() > 0;
        if (cur != null) {
            cur.close();
        }
        return ret;
    }

    /**
     * 查询下载记录是否已经存在
     */
    public synchronized static boolean isExistByPackageName(SQLiteDatabase mSQLiteDatabase, String packageName) {
        boolean ret = false;
        String selection = AppSourceColumns.TABLE_PACKAGENAME + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_DOWNRECORD, null, selection, new String[]{packageName}, null, null, null, null);
        ret = cur != null && cur.getCount() > 0;
        if (cur != null) {
            cur.close();
        }
        return ret;
    }

    /**
     * 更新应用信息
     */
    public synchronized static void updateAppInfo(SQLiteDatabase mSQLiteDatabase, String packageName, String fisrtTypeName, String childTypeName) {
        String selection = Columns.AppInfoColumns.TABLE_PACKAGENAME + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_APP_INFO, null, selection, new String[]{packageName}, null, null, null, null);

        ContentValues cv = new ContentValues();
        cv.put(Columns.AppInfoColumns.TABLE_PACKAGENAME, packageName);
        cv.put(Columns.AppInfoColumns.TABLE_FIRSTTYPENAME, fisrtTypeName);
        cv.put(Columns.AppInfoColumns.TABLE_CHILDTYPENAME, childTypeName);
        if (cur != null && cur.getCount() > 0) {
            Database.update(mSQLiteDatabase, DatabaseAccessor.TABLE_APP_INFO, cv, selection, new String[]{packageName});
        } else {
            Database.insert(mSQLiteDatabase, DatabaseAccessor.TABLE_APP_INFO, cv);
        }
        if (cur != null) {
            cur.close();
        }
    }

    public synchronized static SoftwareInfo querygetAppInfo(SQLiteDatabase mSQLiteDatabase, String packageName) {
        String selection = Columns.AppInfoColumns.TABLE_PACKAGENAME + " =?";
        Cursor cur = null;
        cur = Database.query(mSQLiteDatabase, true, DatabaseAccessor.TABLE_APP_INFO, null, selection, new String[]{packageName}, null, null, null, null);

        SoftwareInfo softwareInfo = null;

        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            softwareInfo = new SoftwareInfo();
            softwareInfo.setPackageName(packageName);
            softwareInfo.setFirstTypeName(cur.getString(cur.getColumnIndex(Columns.AppInfoColumns.TABLE_FIRSTTYPENAME)));
            softwareInfo.setChildTypeName(cur.getString(cur.getColumnIndex(Columns.AppInfoColumns.TABLE_CHILDTYPENAME)));
            cur.close();
            return softwareInfo;
        }

        return null;
    }


}
