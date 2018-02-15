package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by harri on 2018/2/14.
 */

public class Units extends GameObject {
    private int hp;
    private int attack;
    private int defense;
    private int speed;
    private ArrayList<Buff> currentBuffs;

    public Units(int id, String name, Bitmap image, int hp, int attack, int defense, int speed)
    {
        super(id, name, image);
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }
}
