package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Ally extends Units {


    public Ally(int id, String name, Bitmap image, int hp, int attack, int defense, int speed)
    {
        super(id, name, image, hp, attack, defense, speed);
    }
}
