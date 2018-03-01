
package ca.uwaterloo.cw.castlewar.Model;
import ca.uwaterloo.cw.castlewar.Model.Unit;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Ally extends Unit {

    public Ally(int id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(id, name, resource, hp, maxHp, attack, defense, speed, move, minRange, maxRange, cost);
    }

    public static class Archer extends Unit {
        public Archer() {
            super(Id.Unit.ARCHER.ordinal(), "Archer", R.drawable.archer_icon,
                    80, 80, 25, 10, 10, 1, 1, 1, 1);
        }

        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.archer_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.archer_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.archer_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.archer_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.archer_run_004, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.archer_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.archer_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.archer_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.archer_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.archer_run_004, SystemData.PIXEL, SystemData.PIXEL));
        }
    }

    public static class Mage extends Unit {
        public Mage() {
            super(Id.Unit.MAGE.ordinal(), "Mage", R.drawable.mage_icon,
                    50, 50, 30, 0, 0, 1, 1, 1, 2);
        }
        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.mage_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.mage_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.mage_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.mage_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.mage_run_004, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.mage_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.mage_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.mage_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.mage_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.mage_run_004, SystemData.PIXEL, SystemData.PIXEL));
        }
    }

    public static class SwordMan extends Unit {
        public SwordMan() {
            super(Id.Unit.SWORDMAN.ordinal(), "Sword Man", R.drawable.swordman_icon,
                    100, 100, 30, 20, 5, 1, 0, 0, 1);
        }
        @Override
        public void createMovingImage() {
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.swordman_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.swordman_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.swordman_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.swordman_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addRightMovingImage(SystemData.scaleBitmap(R.drawable.swordman_run_004, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.swordman_run_000, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.swordman_run_001, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.swordman_run_002, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.swordman_run_003, SystemData.PIXEL, SystemData.PIXEL));
            addLeftMovingImage(SystemData.flipHorizontally(R.drawable.swordman_run_004, SystemData.PIXEL, SystemData.PIXEL));
        }
    }
}