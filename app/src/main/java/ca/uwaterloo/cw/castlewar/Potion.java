package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Potion extends GameObject {

    private int hpBuff;
    private int attackBuff;
    private int defenseBuff;
    private int speedBuff;
    private int lastingTurn;

    public Potion(int id, String name, Bitmap image, int hpBuff, int attackBuff, int defenseBuff, int speedBuff, int lastingTurn)
    {
        super(id, name, image);
        this.hpBuff = hpBuff;
        this.attackBuff = attackBuff;
        this.defenseBuff = defenseBuff;
        this.speedBuff = speedBuff;
        this.lastingTurn = lastingTurn;
    }
}
