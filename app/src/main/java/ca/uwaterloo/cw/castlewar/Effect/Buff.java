package ca.uwaterloo.cw.castlewar.Effect;


import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Buff extends Effect {

    public Buff(int id, String name,String description, int resource) {
        super(id, name,description, resource);
    }

    public static class AttackBuff extends Buff
    {
        public AttackBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave", "",R.drawable.cross);
        }
    }

    public static class DefenseBuff extends Buff
    {
        public DefenseBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave","", R.drawable.cross);
        }
    }

    public static class SpeedBuff extends Buff
    {
        public SpeedBuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave","", R.drawable.cross);
        }
    }
}
