package com.example.xu_.lab333;

import android.app.Application;

/**
 * Created by xu_ on 2017/10/23.
 */

public class ShopApp extends Application{    //将Application转为public，使其变为全局，别的类也可访问
    public ItemData list_data;
    public ItemData cart_data;
    public int i;
    public MainActivity.MyLvAdapter lvAdapter;

    @Override
    public void onCreate(){
        super.onCreate();    //父类的onCreate
        i = 0;
        list_data = new ItemData(true);   //shift=true
        cart_data = new ItemData(false); //shift=false
    }
}
