package com.example.xu_.lab333;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class mWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.m_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.m_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.m_widget);
        String action = intent.getAction();
        //接收到广播
        if(action.equals("STATICACTION")){
            Bundle bundle = intent.getExtras();
            //设置Widget的显示
            String info = bundle.getString("name")+"仅售"+bundle.getString("price");
            remoteViews.setTextViewText(R.id.appwidget_text, info);
            remoteViews.setImageViewResource(R.id.m_widget_imageView, bundle.getInt("img"));
            //发送到详细信息界面
            Intent intent1 = new Intent(context, ProductInfo.class);
            //intent1.addCategory(Intent.CATEGORY_LAUNCHER);
            intent1.addCategory(Intent.CATEGORY_DEFAULT);
            intent1.putExtra("itemid", bundle.getInt("itemid"));
            //设置pendingintent
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.m_widget, pendingIntent);
            //发送到widget
            ComponentName componentName = new ComponentName(context, mWidget.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }
}

