package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Unit extends GameObject {
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int move;
    private int minRange;
    private int maxRange;
    private int cost;
    private int currentImage;
    private int currentPosition;
    private int moveSpeed;
    private boolean switchImage;
    private boolean isLeft;
    private Unit aim;
    private ArrayList<Bitmap> rightMovingImage;
    private ArrayList<Bitmap> leftMovingImage;
    private ArrayList<Buff> currentBuffs = new ArrayList<>();

    public Unit(String name, SystemData.TypeId type, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(name, type, resource);
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.move = move;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.cost = cost;
        this.rightMovingImage = new ArrayList<>(3);
        this.leftMovingImage = new ArrayList<>(3);
        this.aim = null;
        this.currentPosition = 0;
        this.currentImage = 0;
        this.isLeft = false;
        this.switchImage = false;
        this.moveSpeed = 5;
    }

    abstract protected void createMovingImage();

    public Bitmap getMovingImage()
    {
        if (rightMovingImage.isEmpty())
            createMovingImage();
        else if (leftMovingImage.isEmpty())
            createMovingImage();

        if (isLeft)
            return leftMovingImage.get(currentImage);
        else
            return rightMovingImage.get(currentImage);
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

    public void update()
    {
        // switch moving images
        if (switchImage)
            if (currentImage < 0)
                currentImage = 0;
            else if (currentImage == 0)
            {
                currentImage += 1;
                switchImage = false;
            } else
                currentImage -= 1;
        else if (!switchImage)
            if (rightMovingImage.size() == 0)
                currentImage = 0;
            else if (currentImage >= rightMovingImage.size())
                currentImage = rightMovingImage.size() - 1;
            else if (currentImage == rightMovingImage.size() - 1)
            {
                currentImage -= 1;
                switchImage = true;
            } else
                currentImage += 1;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
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

    public int getCost() {
        return cost;
    }

    public ArrayList<Buff> getCurrentBuffs() {
        return currentBuffs;
    }
}
