package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Unit extends GameObject{
    public final static int ROW = 4;
    public final static int COLUMN = 3;
    public final static int PIXEL = 100;
    private Integer hp;
    private Integer maxHp;
    private Integer attack;
    private Integer defense;
    private Integer speed;
    private Integer move;
    private int minRange;
    private int maxRange;
    private int moveSpeed;
    private boolean isIndexLeft;
    private int currentIndex;
    private boolean isLeft;
    private Integer cost;
    private boolean isPlayer1;
    private Terrain.Tile moveTile;
    private Terrain.Tile actionTile;
    private Unit aim;
    private ArrayList<Bitmap> rightMovingImage;
    private ArrayList<Bitmap> leftMovingImage;
    private int movingImageNum;
    private ArrayList<Buff> currentBuffs = new ArrayList<>();

    public Unit(int id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(id, name, resource);
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.move = move;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.cost = cost;
        this.movingImageNum = 3;
        this.rightMovingImage = new ArrayList<>(movingImageNum);
        this.leftMovingImage = new ArrayList<>(movingImageNum);
        this.aim = null;
        this.isLeft = false;
        this.moveSpeed = 5;
        this.isPlayer1 = true;
        this.currentIndex = 0;
        this.isIndexLeft = true;
        this.moveTile = null;
        this.actionTile = null;
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        setPortrait(Bitmap.createBitmap(original, 32, 0, 32, 32));
    }

    protected void createMovingImage() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        addRightMovingImage(Bitmap.createBitmap(original, 0, 64, 32, 32));
        addRightMovingImage(Bitmap.createBitmap(original, 32, 64, 32, 32));
        addRightMovingImage(Bitmap.createBitmap(original, 64, 64, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 0, 32, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 32, 32, 32, 32));
        addLeftMovingImage(Bitmap.createBitmap(original, 64, 32, 32, 32));
    }

    public Bitmap getMovingImage()
    {
        if (rightMovingImage.isEmpty())
            createMovingImage();
        else if (leftMovingImage.isEmpty())
            createMovingImage();

        if (isLeft)
            return leftMovingImage.get(currentIndex);
        else
            return rightMovingImage.get(currentIndex);
    }

    protected void addRightMovingImage(Bitmap image)
    {
        rightMovingImage.add(image);
        setY(SystemData.getGroundLine() - image.getHeight());
    }


    protected void addLeftMovingImage(Bitmap image)
    {
        leftMovingImage.add(image);
        setY(SystemData.getGroundLine() - image.getHeight());
    }

    // common way to take actions
    // go as near as possible to the first enemy who is the nearest to ally castle
    //
    public void decideStrategy(Terrain terrain)
    {
        // find first available enemy who is the nearest to ally castle

    }



    public void animate()
    {
        if (movingImageNum <= 1) return;
        if (currentIndex == 0)
            isIndexLeft = false;
        else if (currentIndex == movingImageNum - 1)
            isIndexLeft = true;

        if (isIndexLeft)
            currentIndex--;
        else
            currentIndex++;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public void setPlayer1(boolean player1) {
        isPlayer1 = player1;
    }

    synchronized public Integer getSpeed() {
        return speed;
    }

    public int getMove() {
        return move;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }

    synchronized public Integer getHp() {
        return hp;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    synchronized public Integer getAttack() {
        return attack;
    }

    synchronized public Integer getDefense() {
        return defense;
    }

    synchronized public void setHp(int hp) {
        this.hp = hp;
    }

    synchronized public void setAttack(int attack) {
        this.attack = attack;
    }

    synchronized public void setDefense(int defense) {
        this.defense = defense;
    }

    synchronized public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Integer getCost() {
        return cost;
    }


    public ArrayList<Buff> getCurrentBuffs() {
        return currentBuffs;
    }
}
