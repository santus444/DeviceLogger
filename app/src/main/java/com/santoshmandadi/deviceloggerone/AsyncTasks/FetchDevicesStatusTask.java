package com.santoshmandadi.deviceloggerone.AsyncTasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.data.DeviceLoggerContract;
import com.santoshmandadi.deviceloggerone.model.Device;
import com.santoshmandadi.deviceloggerone.model.DeviceObject;
import com.santoshmandadi.deviceloggerone.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class FetchDevicesStatusTask extends AsyncTask<DataSnapshot, Void, Void> {

    public static final String ACTION_DATA_UPDATED = Constants.ACTION_DATA_UPDATED;
    private final String LOG_TAG = FetchDevicesStatusTask.class.getSimpleName();
    private final Context mContext;
    private boolean noResults = false, noNetwork = false;
    private boolean DEBUG = true;

    public FetchDevicesStatusTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to handle insertion of a new device in the device database.
     *
     * @param serialNumber serialNumber used to request devices from the server.
     * @param deviceName   A human-readable device name, e.g "iPhone 5S"
     * @param userName     User Name
     * @return the row ID of the added device.
     */
    long addDevice(String serialNumber, String deviceName, String userName) {
        long deviceTableId = 0;
        Uri uri;
        ContentValues values = new ContentValues();
        values.put(DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER, serialNumber);
        values.put(DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME, deviceName);
        values.put(DeviceLoggerContract.DevicesEntry.COLUMN_USER_NAME, userName);
        uri = mContext.getContentResolver().insert(DeviceLoggerContract.DevicesEntry.CONTENT_URI, values);
        deviceTableId = ContentUris.parseId(uri);
        return deviceTableId;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (noResults)
            Toast.makeText(mContext, R.string.no_results_message, Toast.LENGTH_SHORT).show();
        if (noNetwork)
            Toast.makeText(mContext, R.string.no_network_message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected Void doInBackground(DataSnapshot... params) {

        if (params.length == 0) {
            return null;
        }
        final List<DeviceObject> devicesList = new ArrayList<>();

        DataSnapshot dataSnapshot = params[0];
        Vector<ContentValues> cVVector = new Vector<ContentValues>((int) dataSnapshot.getChildrenCount());

        if (dataSnapshot.getChildrenCount() > 0) {

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Device device = postSnapshot.getValue(Device.class);
                devicesList.add(new DeviceObject(device.getUser(), device.getDeviceName(), device.getSerialNumber()));
                ContentValues deviceValues = new ContentValues();
                deviceValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_USER_NAME, device.getUser());
                deviceValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_DEVICE_NAME, device.getDeviceName());
                deviceValues.put(DeviceLoggerContract.DevicesEntry.COLUMN_SERIAL_NUMBER, device.getSerialNumber());

                cVVector.add(deviceValues);
            }
        } else {
            noResults = true;
        }

        int inserted = 0, deleted = 0;
        deleted = mContext.getContentResolver().delete(DeviceLoggerContract.DevicesEntry.CONTENT_URI, null, null);

        // add to database
        if (cVVector.size() > 0)
            inserted = mContext.getContentResolver().bulkInsert(DeviceLoggerContract.DevicesEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
        else
            noResults = true;
        Log.d(LOG_TAG, "Task Complete. " + deleted + " Deleted in devices");
        Log.d(LOG_TAG, "Task Complete. " + inserted + " Inserted");


        Log.d(LOG_TAG, "Task Complete. " + cVVector.size() + " Inserted");

        return null;
    }
}