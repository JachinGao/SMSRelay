package com.gzc.smsrelay.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaozhaochen on 2018/8/3.
 */

public class SmsOpenHelper extends SQLiteOpenHelper {

    private static String name = "message.db";
    private static int version = 1;

    static final String TABLE_NAME = "msg_list";
    static final String SOURCE = "source";
    static final String DATE = "date";
    static final String MSG_SENDER = "msg_sender";
    static final String MSG_CONTENT = "msg_content";


    SmsOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME +
                " (_id integer primary key autoincrement, "
                + SOURCE + " varchar(10),"
                + DATE + " varchar(10),"
                + MSG_SENDER + " varchar(10),"
                + MSG_CONTENT + " varchar(100))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
