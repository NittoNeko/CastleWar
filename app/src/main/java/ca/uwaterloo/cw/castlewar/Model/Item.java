package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject{
    public final AtomicInteger cost = new AtomicInteger();
    private int buyPrice;
    private int sellPrice;

    public Item(int id, String name, int resource, int buyPrice, int sellPrice, int cost)
    {
        super(id, name, resource);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.cost.set(cost);
    }

    public void use()
    {

    }
}
