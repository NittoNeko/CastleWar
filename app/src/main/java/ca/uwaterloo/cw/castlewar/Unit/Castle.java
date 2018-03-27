package ca.uwaterloo.cw.castlewar.Unit;


import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.Base.Sprite;
import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

public class Castle extends Unit {
    public static final int SIZE = 300;

    public Castle(int id, String name, String description, int resource, Status status) {
        super(id, name, description, resource, status,null,null, Id.Direction.RIGHT, Id.Attack.HIT);
        this.getSprite().disableUnit();
        this.getSprite().addResources(null, null, resource);
        this.getSprite().setConfig(SIZE, SIZE, 4);
        this.getSprite().setY(System.getGroundLine() - SIZE);
    }

    public static Castle createCastle(Id.Castle castle) {
        switch(castle) {
            case HOLY_CASTLE: return new HolyCastle();
            case EVIL_CASTLE: return new EvilCastle();
            default: return null;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Sprite sprite = getSprite();
        canvas.drawBitmap(sprite.getPortrait(), sprite.getX(), sprite.getY(), paint);
    }

    public static class HolyCastle extends Castle {
        public HolyCastle() {
            super(Id.Castle.HOLY_CASTLE.ordinal(),"Holy Castle", "A holy and peaceful castle", R.drawable.castle,
                    new Status(200, 200, 0, 15, 0,0 ,0,0,0));
        }
    }

    public static class EvilCastle extends Castle {
        public EvilCastle() {
            super(Id.Castle.EVIL_CASTLE.ordinal(),"Evil Castle", "An evil and fearful castle", R.drawable.castle,
                    new Status(200, 200, 0, 15, 0,0 ,0,0,0));
        }
    }
}
