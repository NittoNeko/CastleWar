package ca.uwaterloo.cw.castlewar.Model;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Sampler;

import java.lang.String;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import ca.uwaterloo.cw.castlewar.Activity.MultithreadGameLogic;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class GameObject {
    private int id;
    private String name;
    private String description;
    private int resource;
    private int currentIndex;
    private Bitmap portrait;
    private ArrayList<Bitmap> movingImage;
    public final AtomicInteger x = new AtomicInteger();
    private int y;

    public GameObject(int id, String name, int resource) {
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.portrait = null;
        this.currentIndex = 0;
    }

    protected void setDescription(String description)
    {
        this.description = description;
    }

    protected void createPortrait()
    {
        this.portrait = SystemData.scaleIconBitmap(resource, SystemData.PIXEL, SystemData.PIXEL);
    }

    public String getDescription()
    {
        return "Description";
    }

    public Bitmap getPortrait() {
        if (portrait == null) createPortrait();
        return portrait;
    }

    public int getResource() {
        return resource;
    }

    protected void setPortrait(Bitmap portrait) {
        this.portrait = portrait;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
