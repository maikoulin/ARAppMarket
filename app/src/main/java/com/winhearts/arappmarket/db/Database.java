/**
 * Copyright 2011 YaxonNetWork
 */
package com.winhearts.arappmarket.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;

/**
 * 数据库操作
 *
 * @author 柯志言 2013-07-10 创建<br>
 */
public class Database {

    /**
     * 删除一个表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @return
     */
    public void DeleteTable(SQLiteDatabase mSQLiteDatabase, String table) {
        mSQLiteDatabase.execSQL("DROP TABLE " + table);
    }

    /**
     * 清空某个表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @return
     */
    static void clearTable(SQLiteDatabase mSQLiteDatabase, String table) {
        mSQLiteDatabase.execSQL("delete from  " + table);
    }

    /**
     * 根据整型数值匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @return
     */
    public void UpData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                       String condition, Integer value) {
        mSQLiteDatabase.update(table, cv, condition + "=" + Integer.toString(value), null);

    }


    /**
     * 根据整型和字符串 数值匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @param condition2      列名
     * @param value           字符串
     * @return
     */
    public int UpData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                      String condition, Integer value, String condition2, String value2) {
        return mSQLiteDatabase.update(table, cv, condition + "=" + Integer.toString(value) + " and " + condition2 + "=?",
                new String[]{value2});

    }

    /**
     * 根据字符串匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           字符串
     * @return the number of rows affected
     */
    public int UpData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                      String condition, String value) {
        return mSQLiteDatabase.update(table, cv, condition + " = ?", new String[]{value});

    }

    /**
     * 根据两个整型数值匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition1      列名
     * @param index1          数值
     * @param condition2      列名
     * @param index2          数值
     * @return
     */
    public void UpData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                       String condition1, Integer index1, String condition2, Integer index2) {
        mSQLiteDatabase.update(table, cv, condition1 + "=" + Integer.toString(index1) + " and "
                + condition2 + "=" + Integer.toString(index2), null);

    }

    /**
     * 根据三个整型数值匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition1      列名
     * @param index1          数值
     * @param condition2      列名
     * @param index2          数值
     * @param condition3      列名
     * @param index3          数值
     * @return
     */
    public void UpData3(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                        String condition1, Integer index1, String condition2, Integer index2, String condition3, Integer index3) {
        mSQLiteDatabase.update(table, cv,
                condition1 + "=" + Integer.toString(index1) + " and "
                        + condition2 + "=" + Integer.toString(index2) + " and "
                        + condition3 + "=" + Integer.toString(index3), null);

    }

    /**
     * 根据两个整型数值和一个字符串匹配更新表
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition1      列名
     * @param index1          数值
     * @param condition2      列名
     * @param index2          数值
     * @param condition3      列名
     * @param index3          数值
     * @return
     */
    public void UpData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table,
                       String condition1, Integer index1, String condition2, Integer index2, String condition3, String value) {
        mSQLiteDatabase.update(table, cv,
                condition1 + "=" + Integer.toString(index1) + " and "
                        + condition2 + "=" + Integer.toString(index2) + " and "
                        + condition3 + "=" + "=?",
                new String[]{value});

    }

    /**
     * 向表中添加一条数据
     *
     * @param mSQLiteDatabase 数据库对象
     * @param cv              数据内容集合
     * @param table           表名
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long AddData(SQLiteDatabase mSQLiteDatabase, ContentValues cv, String table) {
        return mSQLiteDatabase.insert(table, null, cv);

    }

    /**
     * 根据条件字符串匹配删除表中的数据
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       条件字符串
     */
    public void DeleteData(SQLiteDatabase mSQLiteDatabase, String table,
                           String condition) {
        // mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " +
        // condition);
        mSQLiteDatabase.delete(table, condition, null);
    }

    /**
     * 根据ID列项整型数值匹配删除表中某项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param value           数值
     * @return
     */
    public void DeleteData(SQLiteDatabase mSQLiteDatabase, String table, Integer index) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE _id=" + Integer.toString(index));

    }

    /**
     * 根据整型数值匹配删除表中某项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @return
     */
    public void DeleteDataByCondition(SQLiteDatabase mSQLiteDatabase, String table,
                                      String condition, Integer value) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + "="
                + Integer.toString(value));
    }

    /**
     * 根据整型数值匹配删除表中某项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @param condition2      列名
     * @param value2          数值
     * @return
     */
    public void DeleteDataBy2Condition(SQLiteDatabase mSQLiteDatabase, String table,
                                       String condition, Integer value, String condition2, Integer value2) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + "="
                + Integer.toString(value) + " and " + condition2 + "=" + value2);
    }

    /**
     * 根据整型数值匹配删除表中某项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @param condition2      列名
     * @param value2          数值
     * @return
     */
    public void DeleteDataBy2Condition(SQLiteDatabase mSQLiteDatabase, String table,
                                       String condition, Integer value, String condition2, String value2) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + "="
                + Integer.toString(value) + " and " + condition2 + "= '" + value2 + "'");
    }

    /**
     * 从表中删除不符合条件（字符串）的数据
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param str             字符串
     * @return
     */
    public void DeleteDataByNotLikeCondition(SQLiteDatabase mSQLiteDatabase, String table,
                                             String condition, String str) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + " not like \'%"
                + str + "%\'");
    }

    /**
     * 根据整型数组匹配删除表中项,只要数字在该数组范围内即删除
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param sacle           整形数组
     * @return
     */
    public void DeleteDataByCondition(SQLiteDatabase mSQLiteDatabase, String table,
                                      String condition, Integer[] scale) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + " in ("
                + Arrays.toString(scale).substring(1, Arrays.toString(scale).length() - 1) + ")");
    }

    /**
     * 根据字符串匹配删除表中项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           字符串
     * @return
     */
    public void DeleteDataByStr(SQLiteDatabase mSQLiteDatabase, String table, String condition,
                                String value) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + " = ?",
                new String[]{value});

    }

    /**
     * 根据整型数值匹配删除表中小于多少值的全部项
     *
     * @param mSQLiteDatabase 数据库对象
     * @param table           表名
     * @param condition       列名
     * @param value           数值
     * @return
     */
    public void DeleteDataByValue(SQLiteDatabase mSQLiteDatabase, String table,
                                  String condition, Integer value) {

        mSQLiteDatabase.execSQL("DELETE FROM " + table + " WHERE " + condition + "<"
                + Integer.toString(value));
    }

    /**
     * 删除数据
     *
     * @param mSQLiteDatabase
     * @param table           表名
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public static int delete(SQLiteDatabase mSQLiteDatabase, String table, String whereClause, String[] whereArgs) {

        return mSQLiteDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * 更新数据
     *
     * @param mSQLiteDatabase
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public static int update(SQLiteDatabase mSQLiteDatabase, String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
    }

    /**
     * 新增一条数据
     *
     * @param mSQLiteDatabase
     * @param table
     * @param values
     * @return
     */
    public static long insert(SQLiteDatabase mSQLiteDatabase, String table, ContentValues values) {
        return mSQLiteDatabase.insert(table, null, values);
    }

    public static Cursor query(SQLiteDatabase mSQLiteDatabase, boolean distinct, String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        Cursor cur = null;

        try {
            cur = mSQLiteDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (Exception e) {
            Log.e("query", e.getMessage());
        }

        return cur;
    }

}
