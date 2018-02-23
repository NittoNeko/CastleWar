package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Enemy extends Units {
    private SystemData.EnemyId id;

    public Enemy(SystemData.EnemyId id, String name, Bitmap image, int hp, int maxHp, int attack, int defense, int speed, int cost, int move, int range)
    {
        super(name, SystemData.TypeId.ENEMY, image, hp, maxHp, attack, defense, speed, cost, move, range);
        this.id = id;
    }

    public static class Skeleton extends Enemy
    {
        public Skeleton() {
            super(SystemData.EnemyId.SKELETON, "Skeleton", BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    100, 100, 50, 20, 5, 1, 1, 1);
        }
    }

    public static class Slime extends Enemy
    {
        public Slime() {
            super(SystemData.EnemyId.SLIME, "Slime",BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    30, 30, 80, 0, 0, 2, 1, 2);
        }
    }

    public static class Zombie extends  Enemy
    {
        public Zombie() {
            super(SystemData.EnemyId.ZOMBIE, "Zombie",BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    50, 50, 30, 10, 10, 1, 1, 3);
        }
    }

}
