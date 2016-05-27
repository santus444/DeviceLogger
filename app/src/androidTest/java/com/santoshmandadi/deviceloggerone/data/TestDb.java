package com.santoshmandadi.deviceloggerone.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(DeviceLoggerDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DeviceLoggerContract.DevicesEntry.TABLE_NAME);
        mContext.deleteDatabase(DeviceLoggerDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DeviceLoggerDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without the device entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + DeviceLoggerContract.DevicesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> devicesColumnHashSet = new HashSet<String>();
        devicesColumnHashSet.add(DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER);
        devicesColumnHashSet.add(DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME);
        devicesColumnHashSet.add(DeviceLoggerContract.DevicesEntry.COLUMN_USER_NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            devicesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required device entry columns",
                devicesColumnHashSet.isEmpty());
        db.close();
    }

    public void testDevicesTable() {
        SQLiteDatabase db = new DeviceLoggerDbHelper(this.mContext).getWritableDatabase();
        ContentValues contentValues = TestUtilities.createDeviceDetailValues("123ERT");

        long rowId = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, contentValues);
        Cursor cursor = db.query(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        TestUtilities.validateCursor("Devices validation", cursor, contentValues);
        cursor.close();
        db.close();
    }

    public void testMultiRowDevicesTable() {
        SQLiteDatabase db = new DeviceLoggerDbHelper(this.mContext).getWritableDatabase();
        ContentValues contentValues = TestUtilities.createDeviceDetailValues("123ERT");
        ContentValues contentValues2 = TestUtilities.createDeviceDetailValues("123ER1");

        long rowId = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, contentValues);
        long rowId2 = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, contentValues2);

        Cursor cursor = db.query(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        assertTrue(cursor.getCount() == 2);
        cursor.close();
        db.close();
    }

}
