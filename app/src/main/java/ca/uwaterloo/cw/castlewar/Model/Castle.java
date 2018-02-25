package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Castle extends GameObject {
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    public Castle(int id, String name, int resource, int hp, int maxHp, int attack, int defense)
    {
        super(id, name, resource);
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = 500f / (float) original.getWidth();
        setPortrait(createScaledBitmap(original, 500, (int) (original.getHeight() * ratio), false));
    }

    public static class HolyCastle extends Castle
    {
        public HolyCastle() {
            super(Id.Castle.HOLY.ordinal(),"Holy Castle", R.drawable.castle,
                    200, 200, 50, 25);
        }
    }
}
