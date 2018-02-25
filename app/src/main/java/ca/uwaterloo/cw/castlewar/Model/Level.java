package ca.uwaterloo.cw.castlewar.Model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/15.
 */

public class Level extends GameObject {
    public static class Reward {
        private Item item;
        private int maxNum;
        private int minNum;

        public Reward(Item item, int maxNum, int minNum)
        {
            this.item = item;
            this.maxNum = maxNum;
            this.minNum = minNum;
        }

        public Item getItem() {
            return item;
        }

        public int getMaxNum()
        {
            return this.maxNum;
        }
        public int getMinNum()
        {
            return this.minNum;
        }
    }

    private Item[] items;
    private Unit[] enemies;
    private Reward[] rewards;
    private Terrain terrain;
    private int coinRewards;
    private int maxCost;

    public Level(int id, String name, int resource, Item[] items, Unit[] enemies, Reward[] rewards, Terrain terrain, int coinRewards, int areas, int maxCost)
    {
        super(id, name, resource);
        this.items = items;
        this.enemies = enemies;
        this.rewards = rewards;
        this.terrain = terrain;
        this.coinRewards = coinRewards;
        this.maxCost = maxCost;
    }
    public String getDisplayableEnemies()
    {
        String displayableEnemies = "";

        for (int i = 0; i < enemies.length; ++i)
        {
            displayableEnemies += (i != 0 ? ", " : "") + enemies[i].getName();
        }
        return displayableEnemies;
    }

    public String getDisplayableTerrain()
    {
        return terrain.getName();
    }

    public String getDisplayableRewards()
    {
        String displayableRewards = "";
        for (int i = 0; i < rewards.length; ++i)
        {
            Reward reward = rewards[i];
            displayableRewards += (i != 0 ? ", " : "") +
                    reward.getItem().getName() + " " +
                    Integer.toString(reward.getMinNum()) + "~" +
                    Integer.toString(reward.getMaxNum());
        }
        return displayableRewards;
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = (float) terrain.getBattleFieldsWidth() / (float) original.getWidth();
        setPortrait(createScaledBitmap(original, terrain.getBattleFieldsWidth(), (int) (original.getHeight() * ratio), false));
        setY((SystemData.getScreenHeight() - getPortrait().getHeight()) / 2);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Item[] getItems() {
        return items;
    }

    public Unit[] getEnemies() {
        return enemies;
    }

    public Reward[] getRewards() {
        return rewards;
    }

    public int getCoinRewards() {
        return coinRewards;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public static class Level_1_1 extends Level
    {
        public Level_1_1() {
            super(Id.Level.ONE_ONE.ordinal(), "Level 1-1",
                    R.drawable.forest_background,
                    new Item[] {SystemData.createItem(Id.Item.HP_POTION.ordinal())
                    },
                    new Unit[]{SystemData.createUnit(Id.Unit.SLIME.ordinal()),
                            SystemData.createUnit(Id.Unit.ZOMBIE.ordinal()),
                            SystemData.createUnit(Id.Unit.SKELETON.ordinal())
                    },
                    new Reward[]{new Reward(SystemData.createItem(Id.Item.HP_POTION.ordinal()), 3, 1),
                            new Reward(SystemData.createItem(Id.Item.DEFENSE_POTION.ordinal()), 1, 0)
                    },
                    SystemData.createTerrain(Id.Terrain.FOREST.ordinal()),
                    500, 5, 2);
        }
    }

    public static class Level_1_2 extends Level {
        public Level_1_2() {
            super(Id.Level.ONE_TWO.ordinal(), "Level 1-2",
                    R.drawable.forest_background,
                    new Item[]{SystemData.createItem(Id.Item.HP_POTION.ordinal())
                    },
                    new Unit[]{SystemData.createUnit(Id.Unit.SLIME.ordinal()),
                            SystemData.createUnit(Id.Unit.ZOMBIE.ordinal()),
                            SystemData.createUnit(Id.Unit.SKELETON.ordinal())
                    },
                    new Reward[]{new Reward(SystemData.createItem(Id.Item.HP_POTION.ordinal()), 3, 1),
                            new Reward(SystemData.createItem(Id.Item.DEFENSE_POTION.ordinal()), 1, 0)
                    },
                    SystemData.createTerrain(Id.Terrain.FOREST.ordinal()),
                    500, 5, 2);
        }
    }
}
