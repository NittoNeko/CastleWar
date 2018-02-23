package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.String;

/**
 * Created by harri on 2018/2/14.
 */

public class GameObject {
    private String name;
    private Bitmap image;
    private SystemData.TypeId type;
    private int x;
    private int y;


    public GameObject(String name, SystemData.TypeId type, Bitmap image) {
        this.name = name;
        this.image = image;
        this.type = type;
        this.x = 0;
        this.y = 0;
    }

    public Bitmap getImage() {
        return image;
    }

    public SystemData.TypeId getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
