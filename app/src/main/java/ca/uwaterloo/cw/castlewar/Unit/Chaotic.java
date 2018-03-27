package ca.uwaterloo.cw.castlewar.Unit;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Chaotic extends Unit {
    public Chaotic(int id, String name, String description, int resource, Status status,Integer move ,Integer combat, Id.Direction initialDirection, Id.Attack attack)
    {
        super(id, name, description, resource,status, move, combat, initialDirection, attack);
    }

    public static ArrayList<Unit> getAllChaotic() {
        ArrayList<Unit> chaotic = new ArrayList<>();
        chaotic.add(new Orc());
        chaotic.add(new Slime());
        chaotic.add(new Bat());
        chaotic.add(new Darklord());
        return chaotic;
    }

    public static class Orc extends Chaotic {
        public Orc() {
            super(Id.Chaotic.ORC.ordinal(), "Orc", "123", R.drawable.orc_portrait,
                    new Status(100, 100, 30, 15, 0, 1, 0, 0, 1),
                    R.drawable.orc_move, R.drawable.orc_combat, Id.Direction.LEFT, Id.Attack.HIT);
        }
    }

    public static class Bat extends Chaotic {
        public Bat() {
            super(Id.Chaotic.BAT.ordinal(), "Bat", "123", R.drawable.bat_portrait,
                    new Status(50, 50, 25, 10, 30, 1, 0, 0, 1),
                    R.drawable.bat_move, R.drawable.bat_combat, Id.Direction.RIGHT, Id.Attack.CLAW);
        }
    }

    public static class Slime extends Chaotic {
        public Slime() {
            super(Id.Chaotic.SLIME.ordinal(), "Slime", "123", R.drawable.slime_portrait,
                    new Status(10, 10, 20, 100, 10, 1, 0, 0, 1),
                    R.drawable.slime_move, R.drawable.slime_combat, Id.Direction.LEFT, Id.Attack.HIT);
        }
    }

    public static class Darklord extends Chaotic {
        public Darklord() {
            super(Id.Chaotic.DARKLORD.ordinal(), "Darklord", "123", R.drawable.darklord_portrait,
                    new Status(100, 100, 30, 15, 15, 1, 0, 1, 1),
                    R.drawable.darklord_move, R.drawable.darklord_combat, Id.Direction.RIGHT, Id.Attack.SLASH_FIRE);
        }
    }
}