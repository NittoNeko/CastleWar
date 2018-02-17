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

        if (type == SystemData.TypeId.ALLY.ordinal()) this.image = SystemData.getAllyBitmap(id);
        else if (type == SystemData.TypeId.ENEMY.ordinal()) this.image = SystemData.getEnemyBitmap(id);
        else if (type == SystemData.TypeId.POTION.ordinal()) this.image = SystemData.getPotionBitmap(id);
        else if (type == SystemData.TypeId.TOWER.ordinal()) this.image = SystemData.getTowerBitmap(id);
        else if (type == SystemData.TypeId.BUFF.ordinal()) this.image = SystemData.getBuffBitmap(id);
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
