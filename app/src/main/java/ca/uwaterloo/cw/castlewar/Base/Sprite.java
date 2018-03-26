package ca.uwaterloo.cw.castlewar.Base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ca.uwaterloo.cw.castlewar.Game.Combat;
import ca.uwaterloo.cw.castlewar.Game.Terrain;
import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Structure.Atomic;
import ca.uwaterloo.cw.castlewar.Structure.Id;

/**
 * Created by harri on 2018/3/2downsize.
 */

public class Sprite {
    private Atomic.Id<Id.Direction> indexFlow;
    private Atomic.Id<Id.Image> currentImage;
    private Atomic.Id<Id.Direction> direction;
    private Integer width;
    private Integer height;
    private int downsize;
    private AtomicInteger x;
    private AtomicInteger y;
    private int portraitResource;
    private HashMap<Id.Direction, Bitmap> portrait;
    private HashMap<Id.Image, AtomicInteger> horizonIndex;
    private HashMap<Id.Image, HashMap<Id.Direction, ArrayList<Bitmap>>> horizonBitmaps;
    private HashMap<Id.Image, ArrayList<Integer>> horizonResources;

    public Sprite(int portraitResource) {
        this.x = new AtomicInteger(0);
        this.y = new AtomicInteger(0);
        this.horizonBitmaps = null;
        this.horizonResources = null;
        this.portraitResource = portraitResource;
        this.width = 100;
        this.height = 100;
        this.downsize = 4;
        this.portrait = new HashMap<>();
        this.indexFlow = new Atomic.Id<>(Id.Direction.RIGHT);
        this.currentImage = new Atomic.Id<>(Id.Image.PORTRAIT);
        this.direction = new Atomic.Id<>(Id.Direction.RIGHT);
    }

    public void switchImage(Id.Image image) {
        this.currentImage.set(image);
    }

    public void clone(Sprite source){
        this.x.set(source.x.get());
        this.y.set(source.y.get());
        this.portrait = source.portrait;
        this.horizonBitmaps = source.horizonBitmaps;
        this.horizonIndex = new HashMap<>();
        for (Id.Image image : Id.Image.values()) {
            horizonIndex.put(image, new AtomicInteger(0));
        }
    }

    // set how big the bitmap should be
    // set what times should the original source be shrunk
    // negative number means no change
    public void setConfig(Integer width, Integer height, int downsize) {
        if (downsize > 0) this.downsize = downsize;
        if (width == null || width > 0) this.width = width;
        if (height == null || height > 0) this.height = height;
    }

    // image.PORTRAIT should not be added
    public void addResources(Id.Image image, ArrayList<Integer> resources) {
        if (image == Id.Image.PORTRAIT) return;
        if (horizonResources == null) horizonResources = new HashMap<>();
        horizonResources.put(image, resources);
    }

    // ALL initialization methods should NOT be called often
    // only once when initializing a new game
    // when generating new units, use copy moving image instead
    public void initializeBitmaps(Id.Image image){
        // check resources added
        if (horizonIndex == null) horizonIndex = new HashMap<>();
        if (horizonIndex.get(image) == null) horizonIndex.put(image, new AtomicInteger(0));
        if (horizonResources == null || horizonResources.get(image) == null) return;
        if (horizonBitmaps == null) horizonBitmaps = new HashMap<>();
        if (horizonBitmaps.get(image) == null) horizonBitmaps.put(image, new HashMap<Id.Direction, ArrayList<Bitmap>>());

        horizonBitmaps.get(image).put(Id.Direction.LEFT, new ArrayList<Bitmap>());
        horizonBitmaps.get(image).put(Id.Direction.RIGHT, new ArrayList<Bitmap>());
        for (int resource : horizonResources.get(image)){
            this.horizonBitmaps.get(image).get(Id.Direction.RIGHT).add(System.scaleBitmap(resource, this.width, this.height, this.downsize));
        }
        for (Bitmap bitmap : this.horizonBitmaps.get(image).get(Id.Direction.RIGHT)){
            this.horizonBitmaps.get(image).get(Id.Direction.LEFT).add(System.flipHorizontally(bitmap));
        }
    }

    // extract current bitmap
    public Bitmap getBitmap() {
        if (this.currentImage.get() == Id.Image.PORTRAIT) return getPortrait();
        if (horizonBitmaps.get(this.currentImage.get()) == null) initializeBitmaps(this.currentImage.get());
        return horizonBitmaps.get(this.currentImage.get()).get(direction.get()).get(horizonIndex.get(this.currentImage.get()).get());
    }

    public ArrayList<Bitmap> getBitmaps(Id.Image image, Id.Direction direction) {
        if (this.currentImage.get() == Id.Image.PORTRAIT) return null;
        if (horizonBitmaps.get(this.currentImage.get()) == null) initializeBitmaps(this.currentImage.get());
        return horizonBitmaps.get(image).get(direction);
    }

    public void initializePortrait(Id.Direction direction){
        if (direction == Id.Direction.RIGHT) {
            this.portrait.put(direction, System.scaleBitmap(portraitResource, this.width, this.height, this.downsize));
        } else {
            this.portrait.put(direction, System.flipHorizontally(portraitResource, this.width, this.height, this.downsize));
        }
    }

    public Bitmap getPortrait(Id.Direction direction) {
        if (portrait.get(direction) == null) initializePortrait(direction);
        return portrait.get(direction);
    }

    public Bitmap getPortrait() {
        if (portrait.get(direction.get()) == null) initializePortrait(direction.get());
        return portrait.get(direction.get());
    }

    public void freeAll() {
        if (horizonBitmaps == null) return;
        for (Id.Image image : Id.Image.values()) {
            if (horizonBitmaps.get(image) == null) continue;
            for (Id.Direction direction : Id.Direction.values()) {
                if (horizonBitmaps.get(image).get(direction) == null) continue;
                for (Bitmap bitmap : horizonBitmaps.get(image).get(direction)) {
                    if (bitmap == null) continue;
                    bitmap.recycle();
                }
            }
        }
        horizonBitmaps = null;
    }

    public int getY() {
        return y.get();
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public int getX() {
        return this.x.get();
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public void switchBitmap() {
        Id.Image image = this.currentImage.get();
        if (image == Id.Image.PORTRAIT) return;
        int currentIndex = this.horizonIndex.get(image).get();
        int length = horizonBitmaps.get(image).size();

        if (length <= 1) return;
        if (currentIndex == 0)
            indexFlow.set(Id.Direction.RIGHT);
        else if (currentIndex == length - 1)
            indexFlow.set(Id.Direction.LEFT);

        if (indexFlow.get() == Id.Direction.LEFT)
            currentIndex -= 1;
        else
            currentIndex += 1;

        this.horizonIndex.get(image).set(currentIndex);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(this.getBitmap(), this.getX(), this.getY(), paint);
    }

    public void setDirection(Id.Direction direction) {
        this.direction.set(direction);
    }

    public Id.Direction getDirection() {
        return this.direction.get();
    }

    public static class Target extends Sprite {
        private AtomicBoolean visible;
        private Tile moveTile;
        private int moveSpeed;

        public Target() {
            super(R.drawable.target);
            setY(System.getGroundLine() - getPortrait().getHeight());
            this.moveSpeed = 30;
            this.visible = new AtomicBoolean(false);
        }

        public void move() {
            if (moveTile == null) return;
            if (moveTile.getX() > getX()) {
                // finish move
                if (getX() + moveSpeed > moveTile.getX()) setX(moveTile.getX());
                else setX(getX() + moveSpeed);
            } else if (moveTile.getX() < getX()) {
                // finish move
                if (getX() - moveSpeed < moveTile.getX()) setX(moveTile.getX());
                else setX(getX() - moveSpeed);
            } else {
                // if not just initialized
                // finish move
                moveTile = null;
            }
        }
        public Tile getMoveTile() {
            return moveTile;
        }

        public void setMoveTile(Tile moveTile) {
            this.moveTile = moveTile;
        }

        public boolean isVisible() {
            return visible.get();
        }

        public void setVisible(boolean visible) {
            this.visible.set(visible);
        }
    }
}
