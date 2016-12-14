package com.sms.mcube.smstrack;

/**
 * Created by gousebabjan on 9/12/16.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.sms.mcube.smstrack.syncadapter.SyncAdapter;

import java.util.Random;

public class SimpleWidgetProvider extends AppWidgetProvider implements SmsListener {

    private RemoteViews remoteViews;
    private AppWidgetManager appWidgetManager;
    int[] appWidgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        IncomingSms.bindListener(this);
        SyncAdapter.bindListener(this);
        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.simple_widget);


        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            // String number = String.format("%03d", (new Random().nextInt(900) + 100));


            //  remoteViews.setTextViewText(R.id.textView, number);

//            Intent intent = new Intent(context, SimpleWidgetProvider.class);
//            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);


            remoteViews.setTextViewText(R.id.tv_Sent_count, (Utils.getFromPrefs(context, "COUNT", "0")));
            remoteViews.setTextViewText(R.id.tv_offline_Count, MyApplication.getWritabledatabase().getNoofRows() + "");


            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void UpdateUi(String sentCount, String OfflineCount) {
        remoteViews.setTextViewText(R.id.tv_Sent_count, sentCount);
        remoteViews.setTextViewText(R.id.tv_offline_Count, OfflineCount);
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);

    }

    @Override
    public void messageSent(String sentCount, String OfflineCount) {

        Log.d("Voley", "Sent from syn adapter Widget ");

        UpdateUi(sentCount, OfflineCount);


//        SimpleWidgetProvider.this.runOnUiThread(new Runnable() {
//            public void run() {
//                battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//                UpdateUi(sentCount, OfflineCount);
//                //   Log.d("Voley", "Sent from syn adapter Battery " +battery_level);
//            }
//        });


    }
}