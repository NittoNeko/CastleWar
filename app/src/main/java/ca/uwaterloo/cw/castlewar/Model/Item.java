package ca.uwaterloo.cw.castlewar.Model;

/**
 * Created by harri on 2018/2/15.
 */

public class Item extends GameObject {

    private int buyPrice;
    private int sellPrice;

    public Item(int id, String name, int type, int buyPrice, int sellPrice)
    {
        super(id, name, type);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
}
