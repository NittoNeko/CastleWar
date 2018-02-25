package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Enemy extends Unit {

    public Enemy(Id id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost)
    {
        super(id, name,resource, hp, maxHp, attack, defense, speed, move, minRange, maxRange, cost);
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        setPortrait(Bitmap.createBitmap(original, 32, 0, 32, 32));
    }

    @Override
    protected void createMovingImage() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        addRightMovingImage(Bitmap.createBitmap(original, 0, 64, 32, 32));
        addRightMovingImage(Bitmap.createBitmap(original, 32, 64, 32, 32));
        addRightMovingImage(Bitmap.createBitmap(original, 64, 64, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 0, 32, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 32, 32, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 64, 32, 32, 32));
    }

    public static class Skeleton extends Enemy
    {
        public Skeleton() {
            super(Id.SKELETON, "Skeleton", R.drawable.sword_man,
                    100, 100, 50, 20, 5, 1, 0, 0, 1);
        }
    }

    public static class Slime extends Enemy
    {
        public Slime() {
            super(Id.SLIME, "Slime",R.drawable.archer,
                    30, 30, 80, 0, 0, 2, 0, 0, 1);
        }
    }

    public static class Zombie extends  Enemy
    {
        public Zombie() {
            super(Id.ZOMBIE, "Zombie",R.drawable.mage,
                    50, 50, 30, 10, 10, 1, 1, 1, 1);
        }
    }

}
