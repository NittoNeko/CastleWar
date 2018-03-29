package ca.uwaterloo.cw.castlewar.Game;

import android.util.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import ca.uwaterloo.cw.castlewar.Base.GameObject;
import ca.uwaterloo.cw.castlewar.Item.Potion;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Item.Item;
import ca.uwaterloo.cw.castlewar.Unit.Ability;
import ca.uwaterloo.cw.castlewar.Unit.Chaotic;
import ca.uwaterloo.cw.castlewar.Unit.Lawful;
import ca.uwaterloo.cw.castlewar.Unit.Unit;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Level extends GameObject {
    private Terrain terrain;
    private int unitWave;
    private int itemWave;
    private ArrayList<Integer> unitPattern;
    private ArrayList<Integer> itemPattern;
    private ArrayList<Unit> ally;
    private ArrayList<Unit> enemy;
    private ArrayList<Item> allyPotion;
    private ArrayList<Item> enemyPotion;

    public Level(int id, String name,String description, int resource, Terrain terrain,
                 ArrayList<Unit> ally, ArrayList<Unit> enemy, ArrayList<Item> allyPotion, ArrayList<Item> enemyPotion, ArrayList<Integer> unitPattern, ArrayList<Integer> itemPattern) {
        super(id, name,description, resource);
        this.terrain = terrain;
        this.unitWave = 0;
        this.itemWave = 0;
        this.unitPattern = unitPattern;
        this.itemPattern = itemPattern;
        this.ally = ally;
        this.enemy = enemy;
        this.allyPotion = allyPotion;
        this.enemyPotion = enemyPotion;
    }

    public ArrayList<Unit> getAlly() {
        return ally;
    }

    public ArrayList<Unit> getEnemy() {
        return enemy;
    }

    public ArrayList<Item> getAllyPotion() {
        return allyPotion;
    }

    public ArrayList<Item> getEnemyPotion() {
        return enemyPotion;
    }

    public int getUnitNum() {
        int result = unitPattern.get(unitWave);

        unitWave += 1;
        if (unitWave >= unitPattern.size()) unitWave = 0;

        return result;
    }

    public int getItemNum() {
        int result = itemPattern.get(itemWave);

        itemWave += 1;
        if (itemWave >= itemPattern.size()) itemWave = 0;

        return result;
    }

    public String getDisplayableAlly() {
        String string = "";
        for (int i = 0; i < this.ally.size(); ++i) {
            if (i != 0) string = string + ", ";
            string = string + this.ally.get(i).getName();
        }
        for (int i = 0; i < this.allyPotion.size(); ++i) {
            string = string + ", " + this.allyPotion.get(i).getName();
        }
        return string;
    }

    public String getDisplayableEnemy() {
        String string = "";
        for (int i = 0; i < this.enemy.size(); ++i) {
            if (i != 0) string = string + ", ";
            string = string + this.enemy.get(i).getName();
        }
        for (int i = 0; i < this.enemyPotion.size(); ++i) {
            string = string + ", " + this.enemyPotion.get(i).getName();
        }
        return string;
    }

    public String getDisplayableTerrain()
    {
        return terrain.getDescription();
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public static Level createLevel(Id.Level level) {
        switch(level) {
            case ONE_ONE: return new Level_1();
            case ONE_TWO: return new Level_1();
            case ONE_THREE: return new Level_1();
            case ONE_FOUR: return new Level_1();
            case ONE_FIVE: return new Level_1();
            case ONE_SIX: return new Level_1();
            default: return null;
        }
    }

    public static class Level_1 extends Level {
        public Level_1() {
            super(Id.Level.ONE_ONE.ordinal(), "Level 1", "",
                    0, new Terrain.LongField(),
                    new ArrayList<Unit>(Arrays.asList(new Lawful.SwordMan(), new Lawful.SwordMan(), new Lawful.SwordMan(), new Lawful.SwordMan())),
                    new ArrayList<Unit>(Arrays.asList(new Lawful.SwordMan())),
                    new ArrayList<Item>(Arrays.asList(new Potion.AttackPotion(), new Potion.SpeedPotion(), new Potion.DefensePotion())),
                    new ArrayList<Item>(Arrays.asList(new Potion.HpPotion())),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 3)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 1)));
        }
    }

    public static class Level_2 extends Level {
        public Level_2() {
            super(Id.Level.ONE_TWO.ordinal(), "Level 2", "",
                    0, new Terrain.LongField(),
                    new ArrayList<Unit>(Arrays.asList(new Lawful.SwordMan(), new Lawful.SwordMan(), new Lawful.SwordMan(), new Lawful.SwordMan())),
                    new ArrayList<Unit>(Arrays.asList(new Lawful.SwordMan())),
                    new ArrayList<Item>(Arrays.asList(new Potion.HpPotion())),
                    new ArrayList<Item>(Arrays.asList(new Potion.HpPotion())),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 3)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 1)));
        }
    }
}
