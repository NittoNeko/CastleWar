package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;

import java.lang.String;

/**
 * Created by harri on 2018/2/14.
 */

public class GameObject {
    private int id;
    private String name;
    private Bitmap image;
    private int type;

    public GameObject(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;

        if (type == SystemData.TypeId.ALLY.id()) this.image = SystemData.getAllyBitmap(id);
        else if (type == SystemData.TypeId.ENEMY.id()) this.image = SystemData.getEnemyBitmap(id);
        else if (type == SystemData.TypeId.POTION.id()) this.image = SystemData.getPotionBitmap(id);
        else if (type == SystemData.TypeId.TOWER.id()) this.image = SystemData.getTowerBitmap(id);
        else if (type == SystemData.TypeId.BUFF.id()) this.image = SystemData.getBuffBitmap(id);
        else if (type == SystemData.TypeId.TERRAIN.id()) this.image = SystemData.getTerrainBitmap(id);
        else this.image = null;
    }

    public int getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
