package ca.uwaterloo.cw.castlewar;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Enemy extends Units {

    public Enemy(Context context, int id, String name, int drawable, int hp, int maxHP, int attack, int defense, int speed)
    {
        super(context, id, name, drawable, hp, maxHP, attack, defense, speed);
    }
}
