package com.santoshmandadi.deviceloggerone.model;

import android.view.View;
import android.widget.TextView;

import com.santoshmandadi.deviceloggerone.R;

public class CurrentStatusViewHolder {
    public final TextView deviceName;
    public final TextView userName;


    public CurrentStatusViewHolder(View view) {
        deviceName = (TextView) view.findViewById(R.id.list_item_deviceName);
        userName = (TextView) view.findViewById(R.id.list_item_userName);

    }
}
