package com.santoshmandadi.deviceloggerone.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.santoshmandadi.deviceloggerone.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by santosh on 5/23/16.
 */
public class TestUtilities extends AndroidTestCase {
    public static ContentValues createDeviceDetailValues(String serialNumber) {
        ContentValues testValues = new ContentValues();
        testValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER, serialNumber);
        testValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME, "iPhone 5S");
        testValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_USER_NAME, "Santosh");

        return testValues;
    }

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertDevicesValues(Context context, String serialNumber) {
        DeviceLoggerDbHelper dbHelper = new DeviceLoggerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createDeviceDetailValues(serialNumber);

        long deviceRowId;
        deviceRowId = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Device Values", deviceRowId != -1);

        return deviceRowId;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
