package com.example.xu_.lab333;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by xu_ on 2017/10/24.
 */

public class ProductInfo extends AppCompatActivity{
    ShopApp app;
    TextView price;
    TextView name;
    TextView type;
    TextView info;
    Button add_to_cart;
    Button back;
    Button star;
    ImageView imageView;
    ListView more_info;
    ListView operation;
    int index;
    int item_id;
    DynamicReceiver dynamicReceiver;
    String DYNAMICACTION = "DynamicAction";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        dynamicReceiver = new DynamicReceiver();     //不能在初始化的时候new
        IntentFilter dynamicFilter = new IntentFilter();
        dynamicFilter.addAction(DYNAMICACTION);
        registerReceiver(dynamicReceiver, dynamicFilter);

        init();

        //region 回退
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //endregion

        //region star点击事件
        star_change();
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int star = app.list_data.cart_item.get(index);
                if(star == 1) star = 0;
                else star = 1;
                app.list_data.cart_item.set(index, star);     //设置购物车界面的该商品的星星状态
                star_change();
            }
        });
        //endregion

        //region 加入购物车
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.cart_data.add(
                        app.list_data.data.get(index),
                        app.list_data.cart_item.get(index),
                        app.list_data.itemid.get(index),
                        app.list_data.imgid.get(index)
                );

//                app.lvAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new MessageEvent("refresh"));

                Bundle bundle = new Bundle();
                bundle.putString("name", app.list_data.data.get(index).get("name").toString());
                bundle.putString("price", app.list_data.data.get(index).get("price").toString());
                bundle.putInt("img", app.list_data.imgid.get(index));
                bundle.putInt("itemid", app.list_data.itemid.get(index));
                Intent intentBroadcast = new Intent("DynamicAction");
                intentBroadcast.putExtras(bundle);
                sendBroadcast(intentBroadcast);
                Toast.makeText(ProductInfo.this, "商品已添加到购物车", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion

        final String[] MoreInfo = {"更多产品信息"};
        final String[] Operation = {"一键下单", "分享产品","不感兴趣","查看更多产品促销信息"};
        more_info.setAdapter(new ArrayAdapter<>(ProductInfo.this, R.layout.text, MoreInfo));
        operation.setAdapter(new ArrayAdapter<>(ProductInfo.this, R.layout.text, Operation));

    }

    //region initial
    public void init(){
        app = (ShopApp) getApplication();
        item_id = getIntent().getIntExtra("itemid", 0);
        index = app.list_data.getIndex(item_id);
        price = (TextView) findViewById(R.id.price);
        name = (TextView) findViewById(R.id.name);
        type = (TextView) findViewById(R.id.type);
        info = (TextView) findViewById(R.id.info);
        add_to_cart = (Button) findViewById(R.id.add_to_cart);
        back = (Button) findViewById(R.id.back);
        star = (Button) findViewById(R.id.star);
        imageView = (ImageView) findViewById(R.id.image);
        more_info = (ListView) findViewById(R.id.moreInfo);
        operation = (ListView) findViewById(R.id.operation);

        price.setText(app.list_data.data.get(index).get("price").toString());
        name.setText(app.list_data.data.get(index).get("name").toString());
        type.setText(app.list_data.data.get(index).get("type").toString());
        info.setText(app.list_data.data.get(index).get("info").toString());
        imageView.setImageResource(app.list_data.imgid.get(index));
    }
    //endregion

    //region 星星图片切换
    public void star_change(){
        if (app.list_data.cart_item.get(index) == 0){
            star.setBackgroundResource(R.drawable.empty_star);
        }
        else{
            star.setBackgroundResource(R.drawable.full_star);
        }
    }
    //endregion
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
    }

}
