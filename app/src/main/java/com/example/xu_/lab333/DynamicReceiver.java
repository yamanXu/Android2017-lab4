package com.example.xu_.lab333;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by xu_ on 2017/10/30.
 */

public class DynamicReceiver extends BroadcastReceiver{
    private static final String DYNAMICACTION = "DynamicAction";
    @Override
    public void onReceive(Context context,Intent intent){
        if(intent.getAction().equals(DYNAMICACTION)){
            Bundle bundle = intent.getExtras();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bundle.getInt("img"));
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            String info = bundle.get("name") + "已添加到购物车";
            builder.setContentTitle("马上下单")
                    .setContentText(info)
                    .setTicker("您有一条新消息")
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(bitmap)
                    .setSmallIcon(bundle.getInt("img"))
                    .setAutoCancel(true);
            int selected = bundle.getInt("itemid");
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("mode", 2);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            manager.notify(((ShopApp)context.getApplicationContext()).i++, notification);

            //获取RemoteViews远程修改widget
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.m_widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            remoteViews.setTextViewText(R.id.appwidget_text, info);
            remoteViews.setImageViewResource(R.id.m_widget_imageView, bundle.getInt("img"));
            //设置pendingIntent
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.m_widget, pendingIntent1);
            //发到widget
            ComponentName componentName = new ComponentName(context, mWidget.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }

}
