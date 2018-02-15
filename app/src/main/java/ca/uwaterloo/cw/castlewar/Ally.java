package ca.uwaterloo.cw.castlewar;

import android.content.Context;


/**
 * Created by harri on 2018/2/14.
 */

public class Ally extends Units {


    public Ally(Context context, int id, String name, int drawable, int hp, int maxHP, int attack, int defense, int speed)
    {
        super(context, id, name, drawable, hp, maxHP, attack, defense, speed);
    }
}

public class Swordman extends Ally
{
    public Swordman()
    {
        super(0, "Swordman", , )
    }
}