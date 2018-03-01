package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Castle extends Unit {
    private final static float SIZE = 500;  // must be multiple of Unit.PIXEL
    public Castle(int id, String name, int resource, int hp, int maxHp, int attack, int defense) {
        super(id, name, resource, hp, maxHp, attack, defense, 0, 0,0,1, 0);
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = 500f / (float) original.getWidth();
        setPortrait(createScaledBitmap(original, 500, (int) (original.getHeight() * ratio), false));
        setY(SystemData.getGroundLine() - getPortrait().getHeight());
    }

    public static class HolyCastle extends Castle
    {
        public HolyCastle() {
            super(Id.Unit.HOLY_CASTLE.ordinal(),"Holy Castle", R.drawable.castle,
                    200, 200, 0, 15);
        }
    }
}
