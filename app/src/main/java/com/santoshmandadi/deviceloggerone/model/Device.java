package com.santoshmandadi.deviceloggerone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

public class Device {

    private String user;
    private String serialNumber;
    private String deviceName;
    private HashMap<String, Object> dateLastChanged;

    public Device() {
    }

    public Device(String name, String serialNumber, String user, HashMap<String, Object> dateLastChangedObj) {
        this.deviceName = name;
        this.serialNumber = serialNumber;
        this.user = user;
        this.dateLastChanged = dateLastChangedObj;
    }

    public String getUser() {
        return user;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public HashMap<String, Object> getDateLastChanged() {
        return dateLastChanged;
    }

    @JsonIgnore
    public long getDateLastChangedLong() {

        return (long) dateLastChanged.get("date");
    }
}
