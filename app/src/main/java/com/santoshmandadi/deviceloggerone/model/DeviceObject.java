package com.santoshmandadi.deviceloggerone.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by santosh on 5/24/16.
 */
public class DeviceObject implements Parcelable {
    public static final Parcelable.Creator<DeviceObject> CREATOR
            = new Parcelable.Creator<DeviceObject>() {
        public DeviceObject createFromParcel(Parcel in) {
            return new DeviceObject(in);
        }

        public DeviceObject[] newArray(int size) {
            return new DeviceObject[size];
        }
    };
    private String userName, deviceName, serialNumber;

    private DeviceObject(Parcel in) {
        userName = in.readString();
        deviceName = in.readString();
        serialNumber = in.readString();
    }

    public DeviceObject(String userName, String deviceName, String serialNumber) {
        this.userName = userName;
        this.deviceName = deviceName;
        this.serialNumber = serialNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(deviceName);
        dest.writeString(serialNumber);
    }

    public String geUserName() {
        return userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        return getDeviceName();
    }
}
