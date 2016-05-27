package com.santoshmandadi.deviceloggerone.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DeviceLoggerProvider extends ContentProvider {

    static final int DEVICES_WITH_SERIAL = 103;
    static final int DEVICES = 300;
    private static final SQLiteQueryBuilder sDevicesQueryBuilder;
    private static String LOG_TAG = DeviceLoggerProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static {
        sDevicesQueryBuilder = new SQLiteQueryBuilder();

        sDevicesQueryBuilder.setTables(
                DeviceLoggerContract.DevicesEntry.TABLE_NAME);
    }

    private DeviceLoggerDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        Log.d(LOG_TAG, "Initializing query matching");

        sURIMatcher.addURI(DeviceLoggerContract.CONTENT_AUTHORITY, DeviceLoggerContract.PATH_DEVICES, DEVICES);
        sURIMatcher.addURI(DeviceLoggerContract.CONTENT_AUTHORITY, DeviceLoggerContract.PATH_DEVICES + "/*", DEVICES_WITH_SERIAL);
        return sURIMatcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DEVICES_WITH_SERIAL:
                return DeviceLoggerContract.DevicesEntry.CONTENT_ITEM_TYPE;
            case DEVICES:
                return DeviceLoggerContract.DevicesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        Log.v(LOG_TAG, "Initializing DeviceLoggerDbHelper");

        mOpenHelper = new DeviceLoggerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d(LOG_TAG, "Starting query matching");

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case DEVICES_WITH_SERIAL: {
                retCursor = mOpenHelper.getReadableDatabase().query(DeviceLoggerContract.DevicesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case DEVICES: {
                retCursor = mOpenHelper.getReadableDatabase().query(DeviceLoggerContract.DevicesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        Log.d(LOG_TAG, "Inserted data URI Match: " + match);

        switch (match) {
            case DEVICES: {
                long _id = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "Inserted data ID: " + _id);
                if (_id > 0)
                    returnUri = DeviceLoggerContract.DevicesEntry.buildDeviceUri(values.getAsString(DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case DEVICES: {
                rowsDeleted = db.delete(DeviceLoggerContract.DevicesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numberOfRowsImpacted = 0;

        switch (sUriMatcher.match(uri)) {

            case DEVICES: {
                numberOfRowsImpacted = db.update(DeviceLoggerContract.DevicesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown uri: " + uri);
            }
        }
        if (numberOfRowsImpacted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        //db.close();
        return numberOfRowsImpacted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case DEVICES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DeviceLoggerContract.DevicesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}