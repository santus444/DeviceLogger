package com.santoshmandadi.deviceloggerone.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.santoshmandadi.deviceloggerone.data.DeviceLoggerContract.DevicesEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                DevicesEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DevicesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Devices table during delete", 0, cursor.getCount());
        cursor.close();

    }


    public void deleteAllRecordsFromDB() {
        DeviceLoggerDbHelper dbHelper = new DeviceLoggerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DevicesEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                DeviceLoggerProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: DeviceLoggerProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + DeviceLoggerContract.CONTENT_AUTHORITY,
                    providerInfo.authority, DeviceLoggerContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: DeviceLoggerProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    public void testGetType() {

        String type = mContext.getContentResolver().getType(DevicesEntry.CONTENT_URI);
        assertEquals("Error: the DevicesEntry CONTENT_URI should return DevicesEntry.CONTENT_TYPE",
                DevicesEntry.CONTENT_TYPE, type);
    }

    public void testBasicDevicesQueries() {
        DeviceLoggerDbHelper dbHelper = new DeviceLoggerDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String serialNumber = "123AB";
        ContentValues testValues = TestUtilities.createDeviceDetailValues(serialNumber);
        long deviceRowId = TestUtilities.insertDevicesValues(mContext, serialNumber);
        Cursor deviceCursor = mContext.getContentResolver().query(
                DevicesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Log.d(LOG_TAG, "In Test");

        TestUtilities.validateCursor("testBasicDevicesQueries, device query", deviceCursor, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Device Query did not properly set NotificationUri",
                    deviceCursor.getNotificationUri(), DevicesEntry.CONTENT_URI);
        }
    }


    public void testUpdateDevices() {
        String serialNumber = "123A";
        ContentValues values = TestUtilities.createDeviceDetailValues(serialNumber);

        Uri devicesUri = mContext.getContentResolver().
                insert(DevicesEntry.CONTENT_URI, values);
        String deviceRowId = devicesUri.getLastPathSegment();
        assertTrue(deviceRowId != null);
        Log.d(LOG_TAG, "New row id: " + deviceRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(DevicesEntry.COLUMN_SERIAL_NUMBER, deviceRowId);

        Cursor devicesCursor = mContext.getContentResolver().query(DevicesEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        devicesCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                DevicesEntry.CONTENT_URI, updatedValues, DevicesEntry.COLUMN_SERIAL_NUMBER + "= ?",
                new String[]{deviceRowId});
        assertEquals(1, count);

        tco.waitForNotificationOrFail();

        devicesCursor.unregisterContentObserver(tco);
        devicesCursor.close();
        Cursor cursor = mContext.getContentResolver().query(
                DevicesEntry.CONTENT_URI,
                null,
                DevicesEntry.COLUMN_SERIAL_NUMBER + "=?",
                new String[]{deviceRowId},
                null
        );

        TestUtilities.validateCursor("testUpdateDevices.  Error validating devices entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        String serialNumber = "123A";
        ContentValues testValues = TestUtilities.createDeviceDetailValues(serialNumber);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DevicesEntry.CONTENT_URI, true, tco);
        Uri devicesUri = mContext.getContentResolver().insert(DevicesEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        String devicesRowId = devicesUri.getLastPathSegment();

        assertTrue(devicesRowId != null);


        Cursor cursor = mContext.getContentResolver().query(
                DevicesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating DevicesEntry.",
                cursor, testValues);

    }


    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver devicesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DevicesEntry.CONTENT_URI, true, devicesObserver);


        deleteAllRecordsFromProvider();

        devicesObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(devicesObserver);
    }
}
