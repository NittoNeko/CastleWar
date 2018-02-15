package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Towers extends Units {

    private static final int SPEED = 0;

    public Towers(int id, String name, Bitmap image,int hp, int attack, int defense)
    {
        super(id, name, image, hp, attack, defense, SPEED);
    }
}
