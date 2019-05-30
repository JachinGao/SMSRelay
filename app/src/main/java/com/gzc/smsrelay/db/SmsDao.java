package com.gzc.smsrelay.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gzc.smsrelay.mail.MessageInfo;

import java.util.ArrayList;

import static com.gzc.smsrelay.db.SmsOpenHelper.DATE;
import static com.gzc.smsrelay.db.SmsOpenHelper.MSG_CONTENT;
import static com.gzc.smsrelay.db.SmsOpenHelper.MSG_SENDER;
import static com.gzc.smsrelay.db.SmsOpenHelper.SOURCE;
import static com.gzc.smsrelay.db.SmsOpenHelper.TABLE_NAME;

/**
 * Created by Gaozhaochen on 2018/8/3.
 */

public class SmsDao {

    private static final String TAG = SmsDao.class.getSimpleName();

    private final SQLiteDatabase db;
    private final SmsOpenHelper helper;

    public SmsDao(Context context) {
        helper = new SmsOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insert(String source, String date, String senderName, String content) {

        String sql = "insert into " + TABLE_NAME +
                " ('" + SOURCE + "','" + DATE + "','" + MSG_SENDER + "','" + MSG_CONTENT + "') values (?,?,?,?)";

        db.execSQL(sql, new String[]{source, date, senderName, content});
    }

    public Msg queryByDate(String date) {
        Msg routine;
        String sql = "select * from " + TABLE_NAME + " where " + DATE + " like ? ";
        Cursor cursor = db.rawQuery(sql, new String[]{date});

        if (cursor.moveToNext()) {
            routine = new Msg();
            //routine.date = date;
            routine.date = cursor.getString(1);
            routine.source = cursor.getString(2);
            routine.content = cursor.getString(3);

            Log.e(TAG, "queryByDate: date = " + routine.date + ", start = " + routine.source + ", end = " + routine.content);
        } else {
            routine = null;
        }

        cursor.close();
        return routine;
    }

    public ArrayList<MessageInfo> queryAll() {

        ArrayList<MessageInfo> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME;

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {

            MessageInfo info = new MessageInfo();

            int id = cursor.getInt(0);
            String source = cursor.getString(1);
            String date = cursor.getString(2);
            String sender = cursor.getString(3);
            String content = cursor.getString(4);

            info.setSource(source);
            info.setDate(date);
            info.setSenderName(sender);
            info.setContent(content);

            list.add(info);

            Log.e("query", "query: id = " + id +
                    ", source = " + source + ", date = " + date + ", sender = " + sender + "  content = " + content);
        }

        cursor.close();

        return list;
    }

    public void clear() {
        String sql = "delete from " + TABLE_NAME;
        db.execSQL(sql);
    }

    public void close() {
        db.close();
    }

}
