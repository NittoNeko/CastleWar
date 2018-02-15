package ca.uwaterloo.cw.castlewar;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by harri on 2018/2/14.
 */

public class Units extends GameObject {
    private int hp;
    private int maxHP;
    private int attack;
    private int defense;
    private int speed;
    private ArrayList<Effect> currentEffects;

    public Units(Context context, int id, String name, int drawable, int hp, int maxHP, int attack, int defense, int speed)
    {
        super(context, id, name, drawable);
        this.hp = hp;
        this.maxHP = maxHP;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }
}
