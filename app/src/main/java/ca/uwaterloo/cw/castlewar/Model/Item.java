package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject {
    public final AtomicInteger cost = new AtomicInteger();
    private int buyPrice;
    private int sellPrice;

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



    public boolean Buy(){
        Coin coins =  UserProfile.getCOIN();
        long money = coins.getNum();
        if(money >= this.buyPrice){
            coins.setNum(money - this.buyPrice);
            UserProfile.increaseItemNum(this.getId());
            return true;
        }
        return false;
    }

    public boolean Sell(){
        Coin coins =  UserProfile.getCOIN();
        long money = coins.getNum();
        int num = UserProfile.getItemNum(this.getId());
        if(num > 0){
            coins.setNum(money + this.buyPrice);
            UserProfile.decreaseItemNum(this.getId());
            return true;
        }
        return false;
    }
}
