package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Tower extends Units {
    private SystemData.TowerId id;
    private static final int SPEED = 0;
    private static final int MOVE = 0;
    private static final int RANGE = 0;
    private static final int COST = 0;

    public Tower(String name, SystemData.TowerId id, Bitmap image, int hp, int maxHp, int attack, int defense)
    {
        super(name, SystemData.TypeId.TOWER, image, hp, maxHp, attack, defense, SPEED, MOVE, RANGE, COST);
        this.id = id;
    }

    public static class HolyTower extends Tower
    {
        public HolyTower() {
            super("Holy Tower", SystemData.TowerId.HOLY, BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.sword_man),
                    200, 200, 50, 25);
        }
    }
}
