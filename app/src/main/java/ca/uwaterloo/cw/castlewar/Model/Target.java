package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/23.
 */

public class Target extends GameObject {
    private boolean visible;
    private Terrain.Tile moveTile;
    private int moveSpeed;

    public Target() {
        super(0,"Target", R.drawable.target);
        setY(SystemData.getGroundLine() - getPortrait().getHeight());
        this.moveSpeed = SystemData.PIXEL_PER_UPDATE;
    }

    public void move(){
        if (moveTile == null) return;
        if (moveTile.getX() > x.get()){
        // finish move
        if (x.get() + moveSpeed > moveTile.getX()) x.set(moveTile.getX());
        else x.set(x.get() + moveSpeed);
    } else if (moveTile.getX() < x.get()){
        // finish move
        if (x.get() - moveSpeed < moveTile.getX()) x.set(moveTile.getX());
        else x.set(x.get() - moveSpeed);
    } else{
        // if not just initialized
        // finish move
        moveTile = null;
    }
}

    public Terrain.Tile getMoveTile() {
        return moveTile;
    }

    public void setMoveTile(Terrain.Tile moveTile) {
        this.moveTile = moveTile;
    }

    synchronized public boolean isVisible(){
        return visible;
    }

    synchronized public void setVisible(boolean visible){
        this.visible = visible;
    }
}
