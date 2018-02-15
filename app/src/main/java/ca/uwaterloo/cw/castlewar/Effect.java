package ca.uwaterloo.cw.castlewar;


import android.content.Context;

/**
 * Created by harri on 2018/2/14.
 */

public class Effect extends GameObject {

    private int attack;
    private int defense;
    private int speed;
    private int turnLeft;

    public Effect(Context context, int id, String name, int drawable, int attack, int defense, int speed, int turnLeft)
    {
        super(context, id, name, drawable);
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.turnLeft = turnLeft;
    }
}
