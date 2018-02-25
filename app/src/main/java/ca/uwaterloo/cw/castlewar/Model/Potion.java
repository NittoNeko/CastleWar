package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Potion extends Item {
    private int hpRecover;
    private Buff buff;


    public Potion(int id, String name, int resource, int buyPrice, int sellPrice, Buff buff, int cost)
    {
        super(id, name, resource, buyPrice, sellPrice, cost);
        this.hpRecover = hpRecover;
        this.buff = buff;
    }

    public static class HpPotion extends Potion
    {
        public HpPotion()
        {
            super(Id.Item.HP_POTION.ordinal(), "Health Potion", R.drawable.sword_man,
                    100, 50, null, 1);
        }

    }

    public static class AttackPotion extends Potion
    {
        public AttackPotion()
        {
            super(Id.Item.ATTACK_POTION.ordinal(), "Attack Potion",R.drawable.sword_man,
                    300, 200, null, 1);
        }
    }

    public static class DefensePotion extends Potion
    {
        public DefensePotion()
        {
            super(Id.Item.DEFENSE_POTION.ordinal(), "Defense Potion", R.drawable.sword_man,
                    200, 100, null, 1);
        }
    }

    public static class SpeedPotion extends Potion
    {
        public SpeedPotion()
        {
            super(Id.Item.SPEED_POTION.ordinal(), "Speed Potion",R.drawable.sword_man,
                    100, 50, null, 1);
        }
    }
}
