package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Ally extends Unit {
    private SystemData.AllyId id;

    public Ally(SystemData.AllyId id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(name, SystemData.TypeId.ALLY, resource, hp, maxHp, attack, defense, speed, move, minRange, maxRange, cost);
        this.id = id;
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

    public static class Archer extends Ally {
        public Archer() {
            super(SystemData.AllyId.ARCHER, "Archer",R.drawable.archer,
                    50, 50, 30, 10, 10, 2, 1, 2, 1);
        }

    }

    public static class Mage extends Ally {
        public Mage() {
            super(SystemData.AllyId.MAGE, "Mage",R.drawable.mage,
                    30, 30, 80, 0, 0, 1, 1, 1, 2);
        }
    }

    public static class SwordMan extends Ally {
        public SwordMan() {
            super(SystemData.AllyId.SWORDMAN, "Sword Man",R.drawable.sword_man,
                    100, 100, 50, 20, 5, 1, 0, 0, 1);
        }
    }

}
