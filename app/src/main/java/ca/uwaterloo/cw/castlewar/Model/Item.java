package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject {
    public final AtomicInteger cost = new AtomicInteger();
    private int buyPrice;
    private int sellPrice;
    private long num;

    public Item(int id,String name, int resource, int buyPrice, int sellPrice, int cost)
    {
        super(id, name, resource);
        this.cost.set(cost);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public long getNum() {
        return num;
    }

    public void Buy(){
        Coin coins =  UserProfile.getCOIN();
        long money = coins.getNum();
        if(money >= this.buyPrice){
            coins.setNum(money - this.buyPrice);
            this.num++;
        }
    }

    public void Sell(){
        Coin coins =  UserProfile.getCOIN();
        long money = coins.getNum();
        if(this.num > 0){
            coins.setNum(money + this.buyPrice);
            this.num--;
        }
    }
}
