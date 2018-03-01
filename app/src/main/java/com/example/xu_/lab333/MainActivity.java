package com.example.xu_.lab333;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    protected ShopApp shopapp;
    protected RecyclerView recyclerView;
    protected ListView listView;
    protected FloatingActionButton floatingActionButton;
    protected int mode;    //mode=1 shoppinglist mode=2 cartlist
    protected boolean isScrollToTop;
    public ArrayList<Map<String, Object>> data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);


        init();

        modeShift();

        RecyclerViewInit();

        ListViewInit();

        //region animation
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    isScrollToTop = false;
                }else{
                    isScrollToTop = true;
                }
            }
        });
        //endregion

        Random random = new Random();
        int choose = random.nextInt(10);
        Bundle bundle = new Bundle();

        bundle.putString("name", shopapp.list_data.data.get(choose).get("name").toString());
        bundle.putString("price", shopapp.list_data.data.get(choose).get("price").toString());
        bundle.putInt("img", shopapp.list_data.imgid.get(choose));
        bundle.putInt("itemid", shopapp.list_data.itemid.get(choose));
        Intent intentBroadcast = new Intent("STATICACTION");
        intentBroadcast.putExtras(bundle);
        sendBroadcast(intentBroadcast);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        if(event.msg.equals("refresh")){
            ((MyLvAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        if(intent.hasExtra("mode")){
            mode = getIntent().getIntExtra("mode", 0);
            update_mode();
        }

    }
    public void update_mode(){
        if(mode==1){
            listView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            floatingActionButton.setImageResource(R.mipmap.shoplist);
        }
        else{
            listView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            floatingActionButton.setImageResource(R.mipmap.mainpage);
        }
    }
    //region 初始化
    public void init(){
        shopapp = (ShopApp) getApplication();    //当前运行的application
        recyclerView = (RecyclerView)findViewById(R.id.shoppinglist);
        listView = (ListView) findViewById(R.id.cartlist);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.to_chart);
        mode = 1;
        listView.setVisibility(View.GONE);      //显示商品列表
    }
    //endregion

    //region 界面切换
    public void modeShift(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode==1) mode = 2;           //界面切换
                else mode = 1;
                //设置界面的显示
                if(mode==1){
                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    floatingActionButton.setImageResource(R.drawable.shoplist);
                }
                else{
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    floatingActionButton.setImageResource(R.drawable.mainpage);
                }
            }
        });
    }
    //endregion

    //region RecyclerView
    public void RecyclerViewInit(){
        final MyRcAdapter rcAdapter = new MyRcAdapter(MainActivity.this, R.layout.item,
                new ArrayList<>(shopapp.list_data.data), new ArrayList<>(shopapp.list_data.itemid)); //复制，不能真的删除

        rcAdapter.setOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ProductInfo.class);
                intent.putExtra("itemid", rcAdapter.adapter_id.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
                rcAdapter.adapter_id.remove(position);
                rcAdapter.adapter_data.remove(position);
                rcAdapter.notifyItemRemoved(position);
                rcAdapter.notifyItemRangeChanged(position, rcAdapter.adapter_data.size());
                Toast.makeText(rcAdapter.context, "移除第" + (position+1) + "个商品", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL); //列表的走向：纵向
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); //默认动画
    }
    //endregion

    //region ListView
    public void ListViewInit(){
        final MyLvAdapter lvAdapter = new MyLvAdapter(MainActivity.this, shopapp.cart_data.data, shopapp.cart_data.itemid);
        shopapp.lvAdapter = lvAdapter;            //每次刷新购物车列表
        listView.setAdapter(lvAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ProductInfo.class);
                    intent.putExtra("itemid", shopapp.cart_data.itemid.get(position));  //传参
                    startActivity(intent);      //调用显示
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder ad_builder;
                    ad_builder = new AlertDialog.Builder(MainActivity.this);
                    ad_builder.setTitle("移除商品")
                            .setMessage("从购物车移除" + shopapp.cart_data.data.get(position)
                                    .get("name").toString() + "?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    shopapp.cart_data.data.remove(position);
                                    shopapp.cart_data.itemid.remove(position);
                                    shopapp.cart_data.cart_item.remove(position);
                                    lvAdapter.notifyDataSetChanged();
                                }
                            }).create().show();

                }
                return true;
            }
        });
    }
    //endregion

    public interface MyOnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }

    //region RecyclerView Adapter
    public class MyRcAdapter extends RecyclerView.Adapter<MyRcAdapter.MyRcViewHolder> {
        ArrayList<Map<String, Object>> adapter_data;  //适配器持有的数据
        ArrayList<Integer> adapter_id;                //商品信息
        Context context;                              //上下文
        int adapter_layout;                           //布局id
        MyOnItemClickListener adapter_click;

        public MyRcAdapter(Context context, int layoutId, final ArrayList<Map<String, Object>> data,
                           final ArrayList<Integer> data_id){
            this.context = context;
            this.adapter_layout = layoutId;
            this.adapter_data = data;
            this.adapter_id = data_id;
            this.adapter_click = new MyOnItemClickListener() {      //点击事件
                @Override
                public void onClick(int position) {}
                @Override
                public void onLongClick(int position) {
                    adapter_id.remove(position);
                    adapter_data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, adapter_data.size());
                    Toast.makeText(MyRcAdapter.this.context, "移除第"+(position+1)+"个商品", Toast.LENGTH_SHORT).show();
                }
            };
        }
        //取出map中要用的信息
        public void convert(MyRcViewHolder vh, Map<String, Object> m){   //修改map的信息
            TextView icon = vh.getView(R.id.icon);
            TextView name = vh.getView(R.id.item_name);
            TextView price = vh.getView(R.id.item_price);
            name.setText(m.get("name").toString());
            icon.setText(m.get("first").toString());
            price.setText("");
        }

        public void setOnItemClickListener(MyOnItemClickListener onItemClickListener){
            adapter_click = onItemClickListener;
        }

        @Override
        public void onBindViewHolder(final MyRcViewHolder vh, int pos){
            convert(vh, adapter_data.get(pos));
            if(adapter_click != null){
                vh.v_this.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter_click.onClick(vh.getAdapterPosition());
                    }
                });
                vh.v_this.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        adapter_click.onLongClick(vh.getAdapterPosition());
                        return true;
                    }
                });
            }
            //设置动画
            if(isScrollToTop){
                vh.v_this.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_in_top_anim));
            }
            else{
                vh.v_this.setAnimation(AnimationUtils.loadAnimation(context, R.anim.item_in_bottom_anim));
            }
        }
        //回收动画
        @Override
        public void onViewDetachedFromWindow(MyRcViewHolder holder){
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }
        @Override
        public int getItemCount(){
            return adapter_data.size();
        }

        @Override
        public MyRcViewHolder onCreateViewHolder(ViewGroup viewGroup, int v_type){
            return makeMyRcViewHolder(context, viewGroup, adapter_layout);
        }

        public class MyRcViewHolder extends RecyclerView.ViewHolder{
            public SparseArray<View> v_basket;   //回收站 删除的item放入basket
            public View v_this;          //当前视图

            public MyRcViewHolder(View v_item){   //构造函数
                super(v_item);    //调用父类的构造函数
                v_this = v_item;
                v_basket = new SparseArray<>();
            }
            public <T extends View> T getView(int view_id){  //T为模板类
                View v = v_basket.get(view_id);              //从basket中找id，找到了就拿出来
                if(v == null){                               //快速调用，优化速度
                    v = v_this.findViewById(view_id);
                    v_basket.put(view_id, v);
                }
                return (T) v;
            }
        }
        public MyRcViewHolder makeMyRcViewHolder(Context context, ViewGroup parent, int layout_id){
            return new MyRcViewHolder(LayoutInflater.from(context).inflate(layout_id, parent, false));  //false 装在子视图上
        }
    }
    //endregion

    //region ListView Adapter
    public class MyLvAdapter extends BaseAdapter{
        Context context;
        ArrayList<Map<String, Object>> lv_data;
        ArrayList<Integer> lv_dataid;

        public MyLvAdapter(Context ctx, ArrayList<Map<String, Object>> data, ArrayList<Integer> data_id){
            context = ctx;
            lv_data = data;
            lv_dataid = data_id;
        }

        @Override
        public int getCount(){
            return lv_data.size();
        }
        @Override
        public Map<String, Object> getItem(int i){
            return lv_data.get(i);
        }
        @Override
        public long getItemId(int i){
            return i;
        }
        @Override
        public View getView(int i, View v, ViewGroup viewGroup){
            View view;
            MyLvViewHolder myLvViewHolder;
            if(v == null){
                view = LayoutInflater.from(context).inflate(R.layout.item, null);
                myLvViewHolder = new MyLvViewHolder();
                myLvViewHolder.name = (TextView) view.findViewById(R.id.item_name);
                myLvViewHolder.first = (TextView) view.findViewById(R.id.icon);
                myLvViewHolder.price = (TextView) view.findViewById(R.id.item_price);
                view.setTag(myLvViewHolder);
            }
            else{
                view = v;
                myLvViewHolder = (MyLvViewHolder) view.getTag();
            }
            myLvViewHolder.name.setText(lv_data.get(i).get("name").toString());
            myLvViewHolder.first.setText(lv_data.get(i).get("first").toString());
            myLvViewHolder.price.setText(lv_data.get(i).get("price").toString());

            return view;
        }

        public class MyLvViewHolder{
            public TextView first;
            public TextView name;
            public TextView price;
        }
    }
    //endregion

}
