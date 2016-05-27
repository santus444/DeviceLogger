package com.santoshmandadi.deviceloggerone.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.santoshmandadi.deviceloggerone.R;
import com.santoshmandadi.deviceloggerone.data.DeviceLoggerContract;

import devicelogger.fragments.CurrentStatusFragment;
import devicelogger.fragments.ScanFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)

public class DevicesStatusRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DeviceRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class DeviceRemoteViewsFactory implements RemoteViewsFactory {
        private final String LOG_TAG = DeviceRemoteViewsFactory.class.getSimpleName();
        private Context mContext;
        private Cursor mCursor;
        private int mAppWidgetId;

        public DeviceRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.v(LOG_TAG, "Current status app widget id: " + mAppWidgetId);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }

            Uri baseUri;
            baseUri = DeviceLoggerContract.DevicesEntry.CONTENT_URI;
            final long token = Binder.clearCallingIdentity();
            try {
                mCursor = mContext.getContentResolver().query(baseUri, null, null, null, null);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_devices_list_item);
            if (mCursor.moveToPosition(position)) {
                remoteViews.setTextViewText(R.id.list_item_deviceName, mCursor.getString(CurrentStatusFragment.COL_DEVICE_NAME));
                remoteViews.setTextViewText(R.id.list_item_userName, mCursor.getString(CurrentStatusFragment.COL_USER_NAME));
                if (mCursor.getString(CurrentStatusFragment.COL_USER_NAME).equalsIgnoreCase(ScanFragment.AVAILABLE_SERVER_TAG))
                    remoteViews.setTextColor(R.id.list_item_userName, Color.GREEN);
                else
                    remoteViews.setTextColor(R.id.list_item_userName, Color.RED);

            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
