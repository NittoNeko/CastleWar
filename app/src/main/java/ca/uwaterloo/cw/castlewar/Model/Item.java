package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends CombatObject {

    private int buyPrice;
    private int sellPrice;
    private long num;

    public Item(Id id,String name, int resource, int buyPrice, int sellPrice, int cost)
    {
        super(id, name, resource, cost);
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
