package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Ally extends Units {
    private SystemData.AllyId id;

    public Ally(SystemData.AllyId id, String name, Bitmap image, int hp, int maxHp, int attack, int defense, int speed, int cost, int move, int range) {
        super(name, SystemData.TypeId.ALLY, image, hp, maxHp, attack, defense, speed, cost, move, range);
        this.id = id;
    }

    public static class Archer extends Ally {
        public Archer() {
            super(SystemData.AllyId.ARCHER, "Archer",BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    50, 50, 30, 10, 10, 1, 1, 3);
        }

    }

    public static class Mage extends Ally {
        public Mage() {
            super(SystemData.AllyId.MAGE, "Mage",BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    30, 30, 80, 0, 0, 2, 1, 2);
        }
    }

    public static class SwordMan extends Ally {
        public SwordMan() {
            super(SystemData.AllyId.SWORDMAN, "Sword Man",BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    100, 100, 50, 20, 5, 1, 1, 1);
        }
    }

}
