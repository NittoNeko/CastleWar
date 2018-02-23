package ca.uwaterloo.cw.castlewar.Model;


import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by harri on 2018/2/14.
 */

public class Units extends GameObject {
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int move;
    private int range;
    private int cost;
    private ArrayList<Buff> currentBuffs = new ArrayList<>();

    public Units(String name, SystemData.TypeId type, Bitmap image, int hp, int maxHp, int attack, int defense, int speed, int move, int range, int cost) {
        super(name, type, image);
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.move = move;
        this.range = range;
        this.cost = cost;
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

    public int getRange() {
        return range;
    }

    public int getCost() {
        return cost;
    }

    public ArrayList<Buff> getCurrentBuffs() {
        return currentBuffs;
    }
}
