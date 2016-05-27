package com.santoshmandadi.deviceloggerone.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String DEVICES_QUERY = "12345A";

    private static final Uri TEST_DEVICES_ITEM = DeviceLoggerContract.DevicesEntry.buildDeviceUri(DEVICES_QUERY);
    private static final Uri TEST_DEVICES_DIR = DeviceLoggerContract.DevicesEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = DeviceLoggerProvider.buildUriMatcher();

        assertEquals("Error: The DEVICE URI was matched incorrectly.",
                testMatcher.match(TEST_DEVICES_ITEM), DeviceLoggerProvider.DEVICES_WITH_SERIAL);
        assertEquals("Error: The DEVICES URI was matched incorrectly.",
                testMatcher.match(TEST_DEVICES_DIR), DeviceLoggerProvider.DEVICES);

    }
}
