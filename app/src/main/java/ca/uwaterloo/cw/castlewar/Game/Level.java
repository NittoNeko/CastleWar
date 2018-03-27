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
import ca.uwaterloo.cw.castlewar.Unit.Unit;

/**
 * Created by harri on 2018/2/15.
 */

abstract public class Level extends GameObject {
    private Terrain terrain;
    private int coinRewards;
    private int unitWave;
    private int itemWave;
    private ArrayList<Integer> unitPattern;
    private ArrayList<Integer> itemPattern;
    private ArrayList<Unit> chaotics;
    private ArrayList<Item> potions;

    public Level(int id, String name,String description, int resource, Terrain terrain, int difficulty, int coinRewards, ArrayList<Integer> unitPattern, ArrayList<Integer> itemPattern)
    {
        super(id, name,description, resource);
        this.terrain = terrain;
        this.coinRewards = coinRewards;
        this.unitWave = 0;
        this.itemWave = 0;
        this.unitPattern = unitPattern;
        this.itemPattern = itemPattern;
        this.chaotics = new ArrayList<>();
        this.potions = new ArrayList<>();
    }

    public ArrayList<Unit> currentChaotics() {
        return new ArrayList<>(chaotics);
    }

    public ArrayList<Item> currentPotions() {
        return new ArrayList<>(potions);
    }

    public void addChaotics(Unit chaotics) {
        this.chaotics.add(chaotics);
    }

    public void addPotions(Item potions) {
        this.potions.add(potions);
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

    public String getDisplayableTerrain()
    {
        return terrain.getName();
    }

    public String getDisplayableRewards()
    {
        String displayableRewards = Integer.toString(coinRewards) + " Coins";
        return displayableRewards;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public int getCoinRewards() {
        return coinRewards;
    }

    public static Level createLevel(Id.Level level) {
        switch(level) {
            case ONE_ONE: return new Level_1_1();
            case ONE_TWO: return new Level_1_2();
            case ONE_THREE: return new Level_1_1();
            case ONE_FOUR: return new Level_1_2();
            case ONE_FIVE: return new Level_1_1();
            case ONE_SIX: return new Level_1_2();
            default: return null;
        }
    }

    public static class Level_1_1 extends Level
    {
        public Level_1_1() {
            super(Id.Level.ONE_ONE.ordinal(), "Level 1-1", "good",
                    0, Terrain.createTerrain(Id.Terrain.FOREST),
                    1, 500,
                    new ArrayList<>(Arrays.asList(1, 1, 1, 3)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 1)));
            // decide enemies
            addChaotics(new Chaotic.Orc().setLevel(1).setAllAbility(null, null, null));
            addChaotics(new Chaotic.Slime().setLevel(1).setAllAbility(null, null, null));
            addChaotics(new Chaotic.Darklord().setLevel(1).setAllAbility(null, null, null));
            addChaotics(new Chaotic.Bat().setLevel(1).setAllAbility(null, null, null));

            // decide potions that AI will use
            addPotions(new Potion.HpPotion().setLevel(1));
        }
    }

    public static class Level_1_2 extends Level {
        public Level_1_2() {
            super(Id.Level.ONE_TWO.ordinal(), "Level 1-2", "good",
                    0, Terrain.createTerrain(Id.Terrain.FOREST),
                    2, 1000,
                    new ArrayList<>(Arrays.asList(1, 1, 1, 3)),
                    new ArrayList<>(Arrays.asList(0, 1)));
        }
    }
}
