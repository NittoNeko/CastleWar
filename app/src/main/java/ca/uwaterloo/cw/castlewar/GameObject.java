package ca.uwaterloo.cw.castlewar;

import android.graphics.Bitmap;
import java.lang.String;

/**
 * Created by harri on 2018/2/14.
 */

public class GameObject {
    private int id;
    private String name;
    private Bitmap image;

    public GameObject(int id, String name, Bitmap image)
    {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
