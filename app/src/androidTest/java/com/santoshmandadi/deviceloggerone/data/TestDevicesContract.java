package com.santoshmandadi.deviceloggerone.data;

import android.net.Uri;
import android.test.AndroidTestCase;


public class TestDevicesContract extends AndroidTestCase {

    private static final String TEST_DEVICE = "12342";


    public void testBuildDevices() {
        Uri deviceUri = DeviceLoggerContract.DevicesEntry.buildDeviceUri(TEST_DEVICE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildDeviceUri in " +
                        "DeviceLoggerContract.",
                deviceUri);
        assertEquals("Error: Device serial number not properly appended to the end of the Uri",
                TEST_DEVICE, deviceUri.getLastPathSegment());
        assertEquals("Error: Device serial Uri doesn't match our expected result",
                deviceUri.toString(),
                "content://com.santoshmandadi.deviceloggerone.provider/devices/12342");
    }
}
