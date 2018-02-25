package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends CombatObject{
    private int buyPrice;
    private int sellPrice;

    public Item(Id id, String name, int resource, int buyPrice, int sellPrice, int cost)
    {
        super(id, name, resource, cost);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

}
