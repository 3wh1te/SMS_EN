package com.example.xx.sms_en;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xx on 2017/5/18.
 */
public class DatabaseOp
{
    private static String  tablename ="Key";
  //插入Key
    public static long insertKey(String num,String key,String b,Context con)
    {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNum",num);
        values.put("key",key);
        values.put("bool",b);
        long re = db.insert(tablename, null, values);
        //关闭数据库
        db.close();
        dbh.close();
        return re;
    }
    //查询key
    public static String queryKey(String num,String b,Context con)
    {
        String key = "Empty";
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        String s[] = {num,b};
        Cursor cursor = db.query(tablename,new String[]{"key"},"phoneNum=?and bool=?",s, null, null,null);
        while (cursor.moveToNext()) {
              key = cursor.getString(cursor.getColumnIndex("key"));
        }

        //关闭数据库
        db.close();
        dbh.close();
      return key;
    }
    //更新会话密钥
    public static long updateKey(String num,String key,String b,Context con)
    {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key",key);
        String s[] = {num,b};
       long re = db.update(tablename,values,"phoneNum=? and bool=?",s);
        //关闭连接
        db.close();
        dbh.close();
        return re;
    }
}
