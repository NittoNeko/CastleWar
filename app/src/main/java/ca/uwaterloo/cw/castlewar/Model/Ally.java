package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Ally extends Unit {
    public final static int ROW = 4;
    public final static int COLUMN = 3;
    public final static int PIXEL = 100;

    public Ally(Id id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(id, name, resource, hp, maxHp, attack, defense, speed, move, minRange, maxRange, cost);
    }



    @Override
    protected void createPortrait() {
        int width, height;
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        width = original.getWidth() / COLUMN;
        height = original.getHeight() / ROW;
        setPortrait(Bitmap.createBitmap(original, 1 * width, 0 * height, width, height));
    }

    @Override
    protected void createMovingImage() {
        int width, height;
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        width = original.getWidth() / COLUMN;
        height = original.getHeight() / ROW;
        addRightMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 0 * width, 2 * height, width, height), PIXEL, PIXEL, false));
        addRightMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 1 * width, 2 * height, width, height), PIXEL, PIXEL, false));
        addRightMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 2 * width, 2 * height, width, height), PIXEL, PIXEL, false));
        addLeftMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 0 * width, 1 * height, width, height), PIXEL, PIXEL, false));
        addLeftMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 1 * width, 1 * height, width, height), PIXEL, PIXEL, false));
        addLeftMovingImage(Bitmap.createScaledBitmap(Bitmap.createBitmap(original, 2 * width, 1 * height, width, height), PIXEL, PIXEL, false));
    }

    public static class Archer extends Ally {
        public Archer() {
            super(Id.ARCHER, "Archer",R.drawable.archer,
                    50, 50, 30, 10, 10, 2, 1, 2, 1);
        }

    }

    public static class Mage extends Ally {
        public Mage() {
            super(Id.MAGE, "Mage",R.drawable.mage,
                    30, 30, 80, 0, 0, 1, 1, 1, 2);
        }
    }

    public static class SwordMan extends Ally {
        public SwordMan() {
            super(Id.SWORDMAN, "Sword Man",R.drawable.sword_man,
                    100, 100, 50, 20, 5, 1, 0, 0, 1);
        }
    }

}
