package ca.uwaterloo.cw.castlewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.String;

/**
 * Created by harri on 2018/2/14.
 */

public class GameObject {
    private int id;
    private String name;
    private Bitmap image;
    private Context context;

    public GameObject(Context context, int id, String name, int drawable)
    {
        this.context = context;
        this.id = id;
        this.name = name;
        this.image = BitmapFactory.decodeResource(context.getResources(), drawable);
    }
}
