package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject {

    private int buyPrice;
    private int sellPrice;

    public Item(String name, SystemData.TypeId type, int resource, int buyPrice, int sellPrice)
    {
        super(name, type, resource);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
}
