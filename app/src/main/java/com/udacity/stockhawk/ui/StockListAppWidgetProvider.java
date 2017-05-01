package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;

/**
 * Created With Android Studio
 * User hangox
 * Date 2017/4/24
 * Time 下午7:29
 */

public class StockListAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startUpdateService(context);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_my);

            // Set up the collection
            views.setRemoteAdapter(R.id.listView,
                    new Intent(context, StockItemRemoteViewService.class));
            views.setEmptyView(R.id.listView,R.id.loading);
            // Tell the AppWidgetManager to perform an update on the current app widget
            Intent intent = new Intent(context, StockHistoryActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent historyPendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listView, historyPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void startUpdateService(Context context) {
        context.startService(new Intent(context,QuoteIntentService.class));
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (QuoteIntentService.NOTIFY_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
        }

    }
}
