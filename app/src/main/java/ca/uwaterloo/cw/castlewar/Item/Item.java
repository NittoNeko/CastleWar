package ca.uwaterloo.cw.castlewar.Item;

import ca.uwaterloo.cw.castlewar.Base.GameObject;
import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Base.User;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Item extends GameObject {
    private int basePrice;
    private int level;

    public Item(int id,String name, String description, int resource, int basePrice, Status status) {
        super(id, name, description, resource, status);
        this.basePrice = basePrice;
        this.level = 1;
    }

    public boolean upgrade() {
        int coinLeft = (int) User.getCOIN().getNum() - this.getCurrentPrice();
        if (coinLeft < 0) return false;
        this.level++;
        User.getCOIN().setNum(coinLeft);
        return true;
    }

    public void clone(Item item) {
        this.level = item.level;
    }

    abstract public void use();

    public int getCurrentPrice() {
        return level * basePrice;
    }

    public int getLevel() {
        return level;
    }

    public Item setLevel(int level) {
        this.level = level;
        return this;
    }
}
