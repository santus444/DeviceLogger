package com.santoshmandadi.deviceloggerone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.santoshmandadi.deviceloggerone.data.DeviceLoggerContract.DevicesEntry;

public class DeviceLoggerDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "devices.db";
    private static final int DATABASE_VERSION = 1;

    public DeviceLoggerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DEVICES_TABLE = "CREATE TABLE " + DevicesEntry.TABLE_NAME + " (" +

                DevicesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DevicesEntry.COLUMN_SERIAL_NUMBER + " TEXT NOT NULL," +
                DevicesEntry.COLUMN_DEVICE_NAME + " TEXT NOT NULL, " +
                DevicesEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + DevicesEntry.COLUMN_SERIAL_NUMBER + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_DEVICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DevicesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
