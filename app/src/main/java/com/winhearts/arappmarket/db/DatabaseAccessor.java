package com.winhearts.arappmarket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winhearts.arappmarket.db.Columns.AppSourceColumns;
import com.winhearts.arappmarket.db.Columns.FileRecordColumns;
import com.winhearts.arappmarket.db.Columns.SmartFileDownlogColumns;

import java.util.HashMap;
import java.util.Map;


/**
 * 数据库辅助类
 *
 * @author 柯志言 2013-07-10 创建<br>
 */
@SuppressWarnings("ALL")
public class DatabaseAccessor {

    private static String TAG = "[DatabaseAccessor]";

    private final int DATABASE_VER = 3;
    private DatabaseHelper databaseHelper;
    private static SQLiteDatabase db = null;
    private static DatabaseAccessor instance;
    /* 数据库名 */
    private final String DATABASE_NAME = "easyApp.db";

	/* 表名 */
    /*************************
     * 中心下发的数据表
     ****************************/
    public final static String TABLE_FILERECORD = "table_filerecord";    //文件记录表
    public final static String TABLE_SMARTFILEDOWNLOG = "table_SmartFileDownlog";    //文件下载表
    public final static String TABLE_DOWNRECORD = "table_DownRecord";        //app资源下载记录表
    public final static String TABLE_APP_INFO = "table_app_info";        //应用基本信息
    /*************************
     * 以下为数据库表的SQL语句
     *********************/
    /* 创建节目单表的sql语句 */
    final static String CREATE_PLAYBILL
            = "CREATE TABLE IF NOT EXISTS " + TABLE_FILERECORD
            + " (" + FileRecordColumns._ID + " INTEGER PRIMARY KEY,"
            + FileRecordColumns.TABLE_APPID + " INTEGER,"
            + FileRecordColumns.TABLE_APPNAME + " TEXT,"
            + FileRecordColumns.TABLE_ICONURL + " TEXT,"
            + FileRecordColumns.TABLE_APPSIZE + " TEXT,"
            + FileRecordColumns.TABLE_INTROIMAGE + " TEXT,"
            + FileRecordColumns.TABLE_ABOUT + " TEXT,"
            + FileRecordColumns.TABLE_UPDATEINTRO + " TEXT,"
            + FileRecordColumns.TABLE_AUTHOR + " TEXT,"
            + FileRecordColumns.TABLE_OSDEMAND + " TEXT,"
            + FileRecordColumns.TABLE_APPVERSION + " TEXT,"
            + FileRecordColumns.TABLE_CATEGORYNAME + " TEXT,"
            + FileRecordColumns.TABLE_PUTAWAYTIME + " TEXT,"
            + FileRecordColumns.TABLE_SOURCEID + " TEXT,"
            + FileRecordColumns.TABLE_CATEGORYID + " INTEGER,"
            + FileRecordColumns.TABLE_DOWNLOADNUM + " INTEGER,"
            + FileRecordColumns.TABLE_SCORE + " INTEGER,"
            + FileRecordColumns.TABLE_COMMENTNUM + " INTEGER)";
    /* 创建文件下载表 的sql语句 */
    final static String CREATE_DOWNLOADLIST
            = "CREATE TABLE IF NOT EXISTS " + TABLE_SMARTFILEDOWNLOG
            + " (" + SmartFileDownlogColumns._ID + " INTEGER PRIMARY KEY,"
            + SmartFileDownlogColumns.TABLE_DOWNPATH + " TEXT,"
            + SmartFileDownlogColumns.TABLE_THREADID + " INTEGER,"    //文件当前块序号
            + SmartFileDownlogColumns.TABLE_DOWNLENGTH + " INTEGER)";//块大小（数据块包含的帧数）

    /* 创建APP资源列表 的sql语句 */
    final static String CREATE_APPSOURCE
            = "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNRECORD
            + " (" + AppSourceColumns._ID + " INTEGER PRIMARY KEY,"
            + AppSourceColumns.TABLE_APPNAME + " TEXT,"
            + AppSourceColumns.TABLE_FILENAME + " TEXT,"
            + AppSourceColumns.TABLE_ICONURL + " INTEGER,"
            + AppSourceColumns.TABLE_DOWNLENGTH + " INTEGER,"
            + AppSourceColumns.TABLE_FILESIZE + " INTEGER,"
//            + AppSourceColumns.TABLE_SOURCEID + " INTEGER,"
            + AppSourceColumns.TABLE_STATE + " INTEGER,"
            + AppSourceColumns.TABLE_PACKAGENAME + " TEXT,"
            + AppSourceColumns.TABLE_APPVERSION + " TEXT,"
            + AppSourceColumns.TABLE_DOWNLOADURL + " TEXT)";

    final static String CREATE_APP_INFO
            = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_INFO
            + " (" + AppSourceColumns._ID + " INTEGER PRIMARY KEY,"
            + Columns.AppInfoColumns.TABLE_PACKAGENAME + " TEXT,"
            + Columns.AppInfoColumns.TABLE_FIRSTTYPENAME + " TEXT,"
            + Columns.AppInfoColumns.TABLE_CHILDTYPENAME + " TEXT)";


    /**
     * 创建数据库表
     */
    private void createTableByExecSQL(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYBILL);//节目单
        db.execSQL(CREATE_DOWNLOADLIST);//文件下载表
        db.execSQL(CREATE_APPSOURCE);
        db.execSQL(CREATE_APP_INFO);
    }


    private DatabaseAccessor(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VER);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            createTableByExecSQL(db);
        }


        //数据库更新操作在这里添加,版本号加1
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == DATABASE_VER) {
                db.execSQL(CREATE_APP_INFO);
            }
        }

    }

    public static DatabaseAccessor getInstance(Context application) {
        if (instance == null) {
            instance = new DatabaseAccessor(application);
        }
        return instance;
    }

    public static void clearInstance(Context application) {
        if (instance != null) {
            instance = null;
        }
    }

    /**
     * 打开数据库
     *
     * @return
     */
    public SQLiteDatabase getDB() {

        if (isClose()) {
            Log.i(TAG, "create db");
            try {
                db = databaseHelper.getWritableDatabase();

            } catch (Exception e) {
                //出现异常则删除数据库，并且重新建立数据库
//				String path =File.separator + "data" + File.separator + "data"
//	            + File.separator + "com.socket.client" + File.separator + "databases" + File.separator + PrefsSys.getVersion() + ".db";
//				File file = new File(path);
//				if(file.exists()){
//					file.delete();
//				}
//				MyApplication.getApp().getDatabaseAccessor();
//				db = databaseHelper.getWritableDatabase();
                //db = databaseHelper.getReadableDatabase();
                return null;
            }
        }
        return db;
    }

    public boolean isClose() {
        //noinspection SimplifiableIfStatement
        if (db == null) {
            return true;
        }
        return !db.isOpen();
    }

    public void closeDB() {


        if (db != null) {
            db.close();
            db = null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }


    /**
     * 插入数据
     *
     * @param table  表名
     * @param values 数据
     * @return 插入后的行号
     */
    public long insert(String table, String nullColumeBack, ContentValues values) {

        return getDB().insert(table, nullColumeBack, values);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        getDB().update(table, values, whereClause, whereArgs);
    }


    /**
     * 更新表
     *
     * @param table  表名
     * @param values 更新后的数据
     * @param params 条件
     */
    public void update(String table, ContentValues values, HashMap<String, String> params) {

        //String whereClause=null;
        //String whereArgs[]=null;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
        }
        getDB().update(table, values, sb.toString(), null);

        //getDB().update(table, values, whereClause, whereArgs)
    }


    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor c = getDB().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return c;
    }

    public void delete(String table, String whereClause, String[] whereArgs) {
        getDB().delete(table, whereClause, whereArgs);
    }


}
