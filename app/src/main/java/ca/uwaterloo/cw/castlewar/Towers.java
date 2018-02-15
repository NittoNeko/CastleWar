package ca.uwaterloo.cw.castlewar;

import android.content.Context;

/**
 * Created by harri on 2018/2/14.
 */

public class Towers extends Units {

    private static final int SPEED = 0;

    public Towers(Context context, int id, String name, int drawable, int hp, int maxHP, int attack, int defense)
    {
        super(context, id, name, drawable, hp, maxHP, attack, defense, SPEED);
    }
}
