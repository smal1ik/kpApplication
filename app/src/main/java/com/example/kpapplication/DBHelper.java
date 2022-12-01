package com.example.kpapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    final static String DB_NAME = "worker.db";
    final static String TABLE_NAME = "worker";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME + "(_id INTEGER, display_name TEXT, last_name TEXT, first_name TEXT, " +
                "middle_name TEXT, department TEXT, mail TEXT, inner_phone TEXT, outer_phone TEXT, mobile_phone TEXT, " +
                "post TEXT, room TEXT, birth_date TEXT, account_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}