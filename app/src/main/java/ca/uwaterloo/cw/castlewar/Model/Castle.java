package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Castle extends Unit {
    private SystemData.CastleId id;
    private static final int SPEED = 0;
    private static final int MOVE = 0;
    private static final int RANGE = 0;
    private static final int COST = 0;

    public Castle(String name, SystemData.CastleId id, int resource, int hp, int maxHp, int attack, int defense)
    {
        super(name, SystemData.TypeId.CASTLE, resource, hp, maxHp, attack, defense, SPEED, MOVE, RANGE, RANGE, COST);
        this.id = id;
    }

    @Override
    protected void createMovingImage() {
        addRightMovingImage(BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource()));
        addLeftMovingImage(BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource()));
    }

    public static class HolyCastle extends Castle
    {
        public HolyCastle() {
            super("Holy Castle", SystemData.CastleId.HOLY, R.drawable.castle,
                    200, 200, 50, 25);
        }
    }
}
