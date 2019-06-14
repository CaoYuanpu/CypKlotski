package com.middle.cypklotski;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table user(id varchar(50), name varchar(50), pass int, score text)";
        Log.i("database", "create database=======>");
        db.execSQL(sql);
        ContentValues cv = new ContentValues();
        cv.put("id", "qqq");
        cv.put("name", "Liubei");
        cv.put("pass", 5);
        db.insert("user", null, cv);
        cv = new ContentValues();
        cv.put("id", "www");
        cv.put("name", "Zhangfei");
        cv.put("pass", 4);
        db.insert("user", null, cv);
        cv = new ContentValues();
        cv.put("id", "eee");
        cv.put("name", "Zhaoyun");
        cv.put("pass", 6);
        db.insert("user", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("database", "update database=======>");
    }
}
