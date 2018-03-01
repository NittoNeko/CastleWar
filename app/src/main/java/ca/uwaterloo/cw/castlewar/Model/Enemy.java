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
            super(Id.Unit.SKELETON.ordinal(), "Skeleton", R.drawable.cross,
                    80, 80, 30, 10, 5, 1, 0, 0, 1);
        }
    }

    public static class Slime extends Unit
    {
        public Slime() {
            super(Id.Unit.SLIME.ordinal(), "Slime",R.drawable.cross,
                    10, 10, 20, 50, 10, 2, 0, 1, 1);
        }
    }

    public static class Zombie extends Unit
    {
        public Zombie() {
            super(Id.Unit.ZOMBIE.ordinal(), "Zombie",R.drawable.cross,
                    100, 100, 20, 15, 3, 1, 0, 0, 1);
        }
    }
}