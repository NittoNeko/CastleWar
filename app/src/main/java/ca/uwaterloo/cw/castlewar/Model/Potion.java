package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Potion extends Item {
    private SystemData.PotionId id;
    private int hpRecover;
    private Buff buff;
    private int cost;

    public Potion(SystemData.PotionId id, String name, int resource, int buyPrice, int sellPrice, int hpRecover, Buff buff, int cost)
    {
        super(name, SystemData.TypeId.POTION, resource, buyPrice, sellPrice);
        this.id = id;
        this.hpRecover = hpRecover;
        this.buff = buff;
        this.cost = cost;
    }

    public static class HpPotion extends Potion
    {
        public HpPotion()
        {
            super(SystemData.PotionId.HP, "Health Potion", R.drawable.sword_man,
                    100, 50, 50, null, 1);
        }

    }

    public static class AttackPotion extends Potion
    {
        public AttackPotion()
        {
            super(SystemData.PotionId.ATTACK, "Attack Potion",R.drawable.sword_man,
                    300, 200, 0, null, 1);
        }
    }

    public static class DefensePotion extends Potion
    {
        public DefensePotion()
        {
            super(SystemData.PotionId.DEFENSE, "Defense Potion", R.drawable.sword_man,
                    200, 100, 0, null, 1);
        }
    }

    public static class SpeedPotion extends Potion
    {
        public SpeedPotion()
        {
            super(SystemData.PotionId.SPEED, "Speed Potion",R.drawable.sword_man,
                    100, 50, 0, null, 1);
        }
    }
}
