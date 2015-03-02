package simararora.puautologin.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import simararora.puautologin.R;

/**
 * Created by Simar Arora on 2/14/2015.
 * This App is Licensed under GNU General Public License. A copy of this license can be found in the root of this project.
 */
public class WidgetReceiver extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews views;
        for (int appWidgetId : appWidgetIds) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent loginIntent = new Intent(context, LoginService.class);
            PendingIntent loginPendingIntent = PendingIntent.getService(context, 0, loginIntent, 0);
            Intent logoutIntent = new Intent(context, LogoutService.class);
            PendingIntent logoutPendingIntent = PendingIntent.getService(context, 0, logoutIntent, 0);
            views.setOnClickPendingIntent(R.id.widgetButtonLogin, loginPendingIntent);
            views.setOnClickPendingIntent(R.id.widgetButtonLogout, logoutPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
