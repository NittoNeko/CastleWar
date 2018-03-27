
package ca.uwaterloo.cw.castlewar.Unit;
import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Lawful extends Unit {
    public Lawful(int id, String name, String description, int resource, Status status, Integer move ,Integer combat, Id.Direction initialDirection, Id.Attack attack) {
        super(id, name,description, resource,status, move, combat, initialDirection, attack);
    }

    public static ArrayList<Unit> getAllLawful() {
        ArrayList<Unit> lawful = new ArrayList<>();
        lawful.add(new Archer());
        lawful.add(new Icemage());
        lawful.add(new SwordMan());
        return lawful;
    }

    public static class Archer extends Lawful {
        public Archer() {
            super(Id.Lawful.ARCHER.ordinal(), "Archer", "123", R.drawable.archer_portrait,
                    new Status(80, 80, 25, 10, 10, 1, 1, 1, 1),
                    R.drawable.archer_move, R.drawable.archer_combat, Id.Direction.RIGHT, Id.Attack.ARROW);
        }
    }

    public static class Icemage extends Lawful {
        public Icemage() {
            super(Id.Lawful.MAGE.ordinal(), "Icemage","das", R.drawable.icemage_portrait,
                    new Status(50, 50, 30, 0, 0, 1, 1, 1, 2),
                    R.drawable.icemage_move, R.drawable.icemage_combat, Id.Direction.RIGHT, Id.Attack.ICE_BALST);
        }
    }

    public static class SwordMan extends Lawful {
        public SwordMan() {
            super(Id.Lawful.SWORDMAN.ordinal(), "Sword Man", "dea", R.drawable.swordman_portrait,
                    new Status(100, 100, 30, 20, 5, 1, 0, 0, 1),
                    R.drawable.swordman_move, R.drawable.swordman_combat, Id.Direction.LEFT, Id.Attack.SLASH);
        }
    }

    public static class Bandit extends Lawful
    {
        public Bandit() {
            super(Id.Lawful.SWORDMAN.ordinal(), "Bandit", "dsa", R.drawable.swordman_portrait,
                    new Status(80, 80, 30, 10, 5, 1, 0, 0, 1),
                    R.drawable.swordman_move, R.drawable.swordman_combat, Id.Direction.LEFT, Id.Attack.ICE_BALST);
        }
    }

    public static class Thief extends Lawful
    {
        public Thief() {
            super(Id.Lawful.SWORDMAN.ordinal(), "Thief", "dsaR",R.drawable.swordman_portrait,
                    new Status(50, 50, 40, 10, 10, 2, 0, 0, 1),
                    R.drawable.swordman_move, R.drawable.swordman_combat, Id.Direction.LEFT, Id.Attack.ICE_BALST);
        }
    }

    public static class Ranger extends Lawful
    {
        public Ranger() {
            super(Id.Lawful.SWORDMAN.ordinal(), "Ranger","dsa", R.drawable.swordman_portrait,
                    new Status(40, 40, 30, 10, 5, 1, 1, 1, 1),
                    R.drawable.swordman_move, R.drawable.swordman_combat, Id.Direction.LEFT, Id.Attack.ICE_BALST);
        }
    }
}