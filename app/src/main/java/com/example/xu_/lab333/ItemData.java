package com.example.xu_.lab333;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xu_ on 2017/10/23.
 */

public class ItemData {
    public ArrayList<Map<String, Object>> data;
    public ArrayList<Integer> cart_item;
    public ArrayList<Integer> itemid;
    public ArrayList<Integer> imgid;
    String[] name = {"Enchated Forest", "Arla Milk", "Devondale Milk", "Kindle Oasis", "waitrose 早餐麦片",
            "Mcvitie's 饼干", "Ferrero Rocher", "Maltesers", "Lindt", "Borggreve"};
    String[] price = {"¥5.00", "¥59.00", "¥79.00", "¥2399.00", "¥179.00", "¥14.90", "¥132.59", "¥141.43",
            "¥139.43", "¥28.90"};
    String[] type = {"作者", "产地", "产地", "版本", "重量", "产地", "重量", "重量", "重量", "重量"};
    String[] info = {"Johanna Basford", "德国", "澳大利亚", "8GB", "2Kg", "英国", "300g", "118g", "249g", "640g"};
    String[] first = {"E", "A", "D", "K", "W", "M", "F", "M", "L", "B"};
    Integer[] image = {R.drawable.enchated_forest, R.drawable.arla, R.drawable.devondale,
            R.drawable.kindle, R.drawable.waitrose, R.drawable.mcvitie, R.drawable.ferrero,
            R.drawable.maltesers, R.drawable.lindt, R.drawable.borggreve};

    //region 构造函数constructor
    public ItemData(boolean shift){
        data= new ArrayList<>();
        cart_item = new ArrayList<>();
        itemid = new ArrayList<>();
        imgid = new ArrayList<>();
        if(shift){                                  //shift=true 商品列表
            for(int i=0; i<name.length; i++){
                cart_item.add(0);                   //购物车界面
                itemid.add(i);
                imgid.add(image[i]);
                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("name", name[i]);
                temp.put("price", price[i]);
                temp.put("type", type[i]);
                temp.put("info", info[i]);
                temp.put("first", first[i]);
                temp.put("img", image[i]);
                data.add(temp);
            }
        }
        else{                               //shift = false 购物车列表
            Map<String, Object> temp = new LinkedHashMap<>();
            cart_item.add(-1);
            itemid.add(-1);
            imgid.add(-1);
            temp.put("name", "购物车");
            temp.put("price", "价格");
            temp.put("type", "title");
            temp.put("info", "title");
            temp.put("first", "*");
            temp.put("img", null);
            data.add(temp);
        }
    }
    //endregion

    public void add(Map<String, Object> map, int cart, int item_id, int img_id){
        data.add(map);
        cart_item.add(cart);
        itemid.add(item_id);
        imgid.add(img_id);
    }
    public int getIndex(int position){
        return itemid.indexOf(position);
    }

}
