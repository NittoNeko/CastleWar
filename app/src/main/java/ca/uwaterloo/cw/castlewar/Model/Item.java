package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/15.
 */

public class Item extends GameObject {

    private int buyPrice;
    private int sellPrice;

    public Item(String name, SystemData.TypeId type, Bitmap image, int buyPrice, int sellPrice)
    {
        super(name, type, image);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
}
