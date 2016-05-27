package com.santoshmandadi.deviceloggerone.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.santoshmandadi.deviceloggerone.utils.Constants;

public class DeviceLoggerContract {

    public static final String CONTENT_AUTHORITY = Constants.CONTENT_AUTHORITY;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DEVICES = "devices";

    public static final class DevicesEntry implements BaseColumns {
        public static final String TABLE_NAME = "devices";

        //Column for device name
        public static final String COLUMN_DEVICE_NAME = "device_name";

        //Column for user name
        public static final String COLUMN_USER_NAME = "user_name";

        public static final String COLUMN_SERIAL_NUMBER = "serial_number";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEVICES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEVICES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEVICES;

        public static Uri buildDeviceUri(String serialNumber) {
            return CONTENT_URI.buildUpon().appendPath(serialNumber).build();
        }

    }
}
