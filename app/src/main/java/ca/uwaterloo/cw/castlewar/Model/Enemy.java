package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Enemy extends Unit {
    public Enemy(int id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost)
    {
        super(id, name,resource, hp, maxHp, attack, defense, speed, move, minRange, maxRange, cost);
    }

    public static class Skeleton extends Unit
    {
        public Skeleton() {
            super(Id.Unit.SKELETON.ordinal(), "Skeleton", R.drawable.sword_man,
                    100, 100, 50, 20, 5, 1, 0, 0, 1);
        }
    }

    public static class Slime extends Unit
    {
        public Slime() {
            super(Id.Unit.SLIME.ordinal(), "Slime",R.drawable.archer,
                    30, 30, 80, 0, 0, 2, 0, 0, 1);
        }
    }

    public static class Zombie extends Unit
    {
        public Zombie() {
            super(Id.Unit.ZOMBIE.ordinal(), "Zombie",R.drawable.mage,
                    50, 50, 30, 10, 10, 1, 1, 1, 1);
        }
    }
}