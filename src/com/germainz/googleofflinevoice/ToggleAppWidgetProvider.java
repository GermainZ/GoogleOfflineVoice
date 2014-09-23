package com.germainz.googleofflinevoice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ToggleAppWidgetProvider extends AppWidgetProvider {

    private static SettingsHelper mSettingsHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Common.ACTION_TOGGLE_WIDGET)) {
            if (mSettingsHelper == null)
                mSettingsHelper = new SettingsHelper(context);
            boolean disabled = !mSettingsHelper.isModDisabled();
            mSettingsHelper.setModDisabled(disabled);
            updateWidgets(context, disabled);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (mSettingsHelper == null)
            mSettingsHelper = new SettingsHelper(context);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, ToggleAppWidgetProvider.class);
            intent.setAction(Common.ACTION_TOGGLE_WIDGET);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            boolean disabled = mSettingsHelper.isModDisabled();
            views.setImageViewResource(R.id.icon, disabled ? R.drawable.widget_inactive : R.drawable.widget_active);
            views.setOnClickPendingIntent(R.id.layout, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void updateWidgets(Context context, boolean disabled) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ToggleAppWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setImageViewResource(R.id.icon, disabled ? R.drawable.widget_inactive : R.drawable.widget_active);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
