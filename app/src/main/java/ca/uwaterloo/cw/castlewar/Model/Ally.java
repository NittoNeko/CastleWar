
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
            super(Id.Unit.ARCHER.ordinal(), "Archer", R.drawable.archer,
                    50, 50, 30, 10, 10, 1, 1, 1, 1);
        }

    }

    public static class Mage extends Unit {
        public Mage() {
            super(Id.Unit.MAGE.ordinal(), "Mage", R.drawable.mage,
                    30, 30, 80, 0, 0, 1, 1, 1, 2);
        }
    }

    public static class SwordMan extends Unit {
        public SwordMan() {
            super(Id.Unit.SWORDMAN.ordinal(), "Sword Man", R.drawable.sword_man,
                    100, 100, 50, 20, 5, 1, 0, 0, 1);
        }
    }
}