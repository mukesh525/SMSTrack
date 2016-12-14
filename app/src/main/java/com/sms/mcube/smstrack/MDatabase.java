package com.sms.mcube.smstrack;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gousebabjan on 7/12/16.
 */

public class MDatabase {

    private SiteHelper mHelper;
    private SQLiteDatabase mDatabase;
    private DateFormat dateFormat;

    public MDatabase(Context context) {
        mHelper = new SiteHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void deleteData() {
        mDatabase.delete(SiteHelper.TABLE_SMS, null, null);
    }

    public void delete(String id) {
        mDatabase.execSQL("delete from " + SiteHelper.TABLE_SMS + " where " + SiteHelper.COLUMN_UID + "='" + id + "';");
        Log.d("Voley", "Deleted Successfully "+id);

    }

    public void insertData(SmsData smsData) {
        String sql = "INSERT INTO " + SiteHelper.TABLE_SMS + " VALUES (?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);

        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindString(2, smsData.getTo());
        statement.bindString(3, smsData.getFrom());
        statement.bindString(4, smsData.getText());
        statement.bindString(5, smsData.getTime());
        statement.execute();

        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<SmsData> getData() {
        ArrayList<SmsData> dataList = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_TO,
                SiteHelper.COLUMN_FROM,
                SiteHelper.COLUMN_TEXT,
                SiteHelper.COLUMN_TIME


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_SMS, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                SmsData data = new SmsData();
                data.setId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_UID)));
                data.setTo(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_TO)));
                data.setFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_FROM)));
                data.setText(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_TEXT)));
                data.setTime(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_TIME)));

                dataList.add(data);
            }
            while (cursor.moveToNext());
        }

        return dataList;
    }


    public long getNoofRows() {
        return  DatabaseUtils.queryNumEntries(mDatabase, SiteHelper.TABLE_SMS);
    }

    private static class SiteHelper extends SQLiteOpenHelper {
        public static final String TABLE_SMS = "sms";
        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_TO = "landing_number";
        public static final String COLUMN_FROM = "sender";
        public static final String COLUMN_TEXT = "message";
        public static final String COLUMN_TIME = "msg_time";


        private static final String CREATE_TABLE_SMSTRACK = "CREATE TABLE " + TABLE_SMS + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TO + " TEXT," +
                COLUMN_FROM + " TEXT," +
                COLUMN_TEXT + " TEXT," +
                COLUMN_TIME + " TEXT" +
                ");";


        private static final String DB_NAME = "MCubeSMS.db";
        private static final int DB_VERSION = 2;

        private Context mContext;

        public SiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_SMSTRACK);
            Log.d("TABLE", "onCreate Called");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SMSTRACK);
            onCreate(db);
        }
    }


}


