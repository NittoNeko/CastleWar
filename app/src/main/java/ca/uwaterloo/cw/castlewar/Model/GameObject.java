package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.String;
import java.util.ArrayList;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class GameObject {
    private Id id;
    private String name;
    private String description;
    private int resource;
    private Bitmap portrait;
    private ArrayList<Bitmap> rightMovingImage;
    private ArrayList<Bitmap> leftMovingImage;
    private int x;
    private int y;


    public GameObject(Id id, String name, int resource) {
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.portrait = null;
    }

    protected void setDescription(String description)
    {
        this.description = description;
    }

    protected void createPortrait()
    {
        this.portrait = BitmapFactory.decodeResource(SystemData.getContext().getResources(), resource);
    }

    public String getDescription()
    {
        return description;
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

    public Id getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
