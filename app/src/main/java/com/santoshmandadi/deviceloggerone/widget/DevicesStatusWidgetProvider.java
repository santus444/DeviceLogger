package com.santoshmandadi.deviceloggerone.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.santoshmandadi.deviceloggerone.AsyncTasks.FetchDevicesStatusTask;
import com.santoshmandadi.deviceloggerone.MainActivity;
import com.santoshmandadi.deviceloggerone.R;


public class DevicesStatusWidgetProvider extends AppWidgetProvider {

    private String LOG_TAG = DevicesStatusWidgetProvider.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(LOG_TAG, "Inside onUpdate Method");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_devices_status);
            Intent serviceIntent = new Intent(context, DevicesStatusRemoteViewService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startService(serviceIntent);

            remoteViews.setRemoteAdapter(appWidgetId, R.id.widget_devices_listview, serviceIntent);
            remoteViews.setEmptyView(R.id.widget_devices_listview, R.id.widget_devices_status_empty);
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_header, pendingIntent);
            PendingIntent pendingIntentEmpty = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_devices_status_empty, pendingIntentEmpty);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG, "Inside onReceive Method");
        String action = intent.getAction();

        if (FetchDevicesStatusTask.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_devices_listview);
        }
    }
}
