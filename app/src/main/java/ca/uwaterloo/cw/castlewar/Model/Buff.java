package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Buff extends GameObject {
    private int attack;
    private int defense;
    private int speed;
    private int turnLeft;

    public Buff(int id, String name, int resource, int attack, int defense, int speed, int turnLeft)
    {
        super(id, name, resource);
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.turnLeft = turnLeft;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getTurnLeft() {
        return turnLeft;
    }

    public static class AttackBuff extends Buff
    {
        public AttackBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave", R.drawable.sword_man,
                    20, 0, 0, 3);
        }
    }

    public static class DefenseBuff extends Buff
    {
        public DefenseBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave", R.drawable.sword_man,
                    0, 10, 0, 3);
        }
    }

    public static class SpeedBuff extends Buff
    {
        public SpeedBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave", R.drawable.sword_man,
                    0, 0, 5, 3);
        }
    }
}
