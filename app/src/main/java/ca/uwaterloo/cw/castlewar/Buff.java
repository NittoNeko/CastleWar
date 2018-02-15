package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Buff extends GameObject {

    private int attack;
    private int defense;
    private int speed;
    private int turnLeft;

    public Buff(int id, String name, Bitmap image, int attack, int defense, int speed, int turnLeft)
    {
        super(id, name, image);
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.turnLeft = turnLeft;
    }
}
