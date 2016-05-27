package com.santoshmandadi.deviceloggerone.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.model.CurrentStatusViewHolder;

import devicelogger.fragments.CurrentStatusFragment;
import devicelogger.fragments.ScanFragment;

/**
 * {@link DevicesAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class DevicesAdapter extends CursorAdapter {

    public DevicesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        Views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_device_status, parent, false);
        CurrentStatusViewHolder currentStatusFragmentViewHolder = new CurrentStatusViewHolder(view);
        view.setTag(currentStatusFragmentViewHolder);
        return view;
    }

    /*
       fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        CurrentStatusViewHolder currentStatusFragmentViewHolder = (CurrentStatusViewHolder) view.getTag();
        String deviceName = cursor.getString(CurrentStatusFragment.COL_DEVICE_NAME);
        if (!deviceName.equalsIgnoreCase(""))
            currentStatusFragmentViewHolder.deviceName.setText(deviceName);
        String userName = cursor.getString(CurrentStatusFragment.COL_USER_NAME);
        if (userName.equalsIgnoreCase(ScanFragment.AVAILABLE_SERVER_TAG)) {
            view.setContentDescription(deviceName + " is " + userName);
            currentStatusFragmentViewHolder.userName.setTextColor(Color.GREEN);
            currentStatusFragmentViewHolder.userName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            view.setContentDescription(deviceName + " is with " + userName);
            currentStatusFragmentViewHolder.userName.setTextColor(Color.RED);
            currentStatusFragmentViewHolder.userName.setTypeface(Typeface.DEFAULT_BOLD);
        }
        currentStatusFragmentViewHolder.userName.setText(userName);


    }
}