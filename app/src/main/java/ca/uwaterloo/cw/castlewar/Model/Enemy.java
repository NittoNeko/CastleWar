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

    public static class Bandit extends Unit
    {
        public Bandit() {
            super(Id.Unit.BANDIT.ordinal(), "Bandit", R.drawable.bandit_icon,
                    80, 80, 30, 10, 5, 1, 0, 0, 1);
        }
        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.bandit_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.bandit_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.bandit_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.bandit_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.bandit_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.bandit_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.bandit_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.bandit_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.bandit_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.bandit_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
        }
    }

    public static class Theif extends Unit
    {
        public Theif() {
            super(Id.Unit.THEIF.ordinal(), "Theif",R.drawable.theif_icon,
                    50, 50, 40, 10, 10, 2, 0, 0, 1);
        }
        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.theif_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.theif_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.theif_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.theif_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.theif_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.theif_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.theif_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.theif_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.theif_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.theif_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
        }
    }

    public static class Ranger extends Unit
    {
        public Ranger() {
            super(Id.Unit.RANGER.ordinal(), "Ranger",R.drawable.ranger_icon,
                    40, 40, 30, 10, 5, 1, 1, 1, 1);
        }
        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.ranger_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.ranger_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.ranger_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.ranger_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.ranger_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.ranger_run_000, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.ranger_run_001, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.ranger_run_002, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.ranger_run_003, SystemData.PIXEL, SystemData.PIXEL,4));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.ranger_run_004, SystemData.PIXEL, SystemData.PIXEL,4));
        }
    }
}