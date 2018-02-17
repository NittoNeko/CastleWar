package ca.uwaterloo.cw.castlewar.Model;


import android.support.annotation.NonNull;

/**
 * Created by harri on 2018/2/14.
 */

public class Ally extends Units {

    public Ally(int id, String name, int hp, int maxHp, int attack, int defense, int speed, int cost, int move, int range) {
        super(id, name, SystemData.TypeId.ALLY.id(), hp, maxHp, attack, defense, speed, cost, move, range);
    }

    public static class Archer extends Ally {
        public Archer() {
            super(SystemData.AllyId.ARCHER.id(), "Archer",50, 50, 30, 10, 10, 1, 1, 3);
        }

    }

    public static class Mage extends Ally {
        public Mage() {
            super(SystemData.AllyId.MAGE.id(), "Mage",30, 30, 80, 0, 0, 2, 1, 2);
        }
    }

    public static class SwordMan extends Ally {
        public SwordMan() {
            super(SystemData.AllyId.SWORDMAN.id(), "Sword Man",100, 100, 50, 20, 5, 1, 1, 1);
        }
    }

}
