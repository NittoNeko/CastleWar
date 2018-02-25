package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/14.
 */

public class Castle extends Unit {
    private static final int SPEED = 0;
    private static final int MOVE = 0;
    private static final int RANGE = 0;
    private static final int COST = 0;

    public Castle(Id id, String name, int resource, int hp, int maxHp, int attack, int defense)
    {
        super(id, name, resource, hp, maxHp, attack, defense, SPEED, MOVE, RANGE, RANGE, COST);
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = 500f / (float) original.getWidth();
        setPortrait(createScaledBitmap(original, 500, (int) (original.getHeight() * ratio), false));
    }

    @Override
    protected void createMovingImage() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = 500f / (float) original.getWidth();
        addRightMovingImage(createScaledBitmap(original, 500, (int) (original.getHeight() * ratio), false));
        addLeftMovingImage(createScaledBitmap(original, 500, (int) (original.getHeight() * ratio), false));
    }

    public static class HolyCastle extends Castle
    {
        public HolyCastle() {
            super(Id.HOLY_CASTLE,"Holy Castle", R.drawable.castle,
                    200, 200, 50, 25);
        }
    }
}
