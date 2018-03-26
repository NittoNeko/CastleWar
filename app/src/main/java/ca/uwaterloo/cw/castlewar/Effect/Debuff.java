package ca.uwaterloo.cw.castlewar.Effect;

import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Structure.Id;

/**
 * Created by harri on 2018/3/24.
 */

abstract public class Debuff extends Effect {
    public Debuff(int id, String name,String description, int resource) {
        super(id, name,description, resource);
    }

    public static class AttackDebuff extends Debuff
    {
        public AttackDebuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave", "", R.drawable.cross);
        }
    }

    public static class DefenseDebuff extends Debuff
    {
        public DefenseDebuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave","", R.drawable.cross);
        }
    }

    public static class SpeedDebuff extends Debuff
    {
        public SpeedDebuff()
        {
            super(Id.Buff.ATTACK.ordinal(), "Brave","", R.drawable.cross);
        }
    }
}
