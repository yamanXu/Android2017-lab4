package com.example.xu_.lab333;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

/**
 * Created by xu_ on 2017/10/30.
 */

public class StaticReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        //接收到一个广播
        if(intent.getAction().equals("STATICACTION")){
            Bundle bundle = intent.getExtras();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bundle.getInt("img"));
            //获取通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //实例化通知栏构造器
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            //对builder进行配置
            builder.setContentTitle("新商品热卖")
                    .setContentText(bundle.getString("name")+"仅售"+bundle.getString("price")+"!")
                    .setTicker("您有一条新消息")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(bundle.getInt("img"))
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true);
            //绑定intent，点击进入商品详情界面
            Intent intent1 = new Intent(context, ProductInfo.class);
            int selected = bundle.getInt("itemid");
            intent1.putExtra("itemid", selected);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
            //绑定Notification，发送通知请求
            Notification notification = builder.build();
            manager.notify(0, notification);
        }
    }

}
