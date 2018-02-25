package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject{
    private int cost;
    private int buyPrice;
    private int sellPrice;

    public Item(int id, String name, int resource, int buyPrice, int sellPrice, int cost)
    {
        super(id, name, resource);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.cost = cost;
    }

    public void use()
    {

    }

    public int getCost() {
        return cost;
    }
}
