package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/15.
 */

public class Level extends GameObject {
    private SystemData.LevelId id;

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

    private ArrayList<Enemy> enemies;
    private ArrayList<Reward> rewards;
    private Terrain terrain;
    private int coinRewards;
    private int areas;
    private int maxCost;

    public Level(SystemData.LevelId id, String name, Bitmap image, ArrayList<Enemy> enemies, ArrayList<Reward> rewards, Terrain terrain, int coinRewards, int areas, int maxCost)
    {
        super(name, SystemData.TypeId.LEVEL, image);
        this.id = id;
        this.enemies = enemies;
        this.rewards = rewards;
        this.terrain = terrain;
        this.coinRewards = coinRewards;
        this.areas = areas;
        this.maxCost = maxCost;
    }
    public String getDisplayableEnemies()
    {
        String displayableEnemies = "";

        for (int i = 0; i < enemies.size(); ++i)
        {
            displayableEnemies += (i != 0 ? ", " : "") + enemies.get(i).getName();
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
        for (int i = 0; i < rewards.size(); ++i)
        {
            Reward reward = rewards.get(i);
            displayableRewards += (i != 0 ? ", " : "") +
                    reward.getItem().getName() + " " +
                    Integer.toString(reward.getMinNum()) + "~" +
                    Integer.toString(reward.getMaxNum());
        }
        return displayableRewards;
    }

    public SystemData.LevelId getId() {
        return id;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public static class Level_1_1 extends Level
    {
        public Level_1_1() {
            super(SystemData.LevelId.ONE_ONE, "Level 1-1",
                    BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.forest_background),
                    new ArrayList<Enemy>(Arrays.asList(new Enemy[]{
                            SystemData.create(SystemData.EnemyId.SKELETON),
                            SystemData.create(SystemData.EnemyId.ZOMBIE),
                            SystemData.create(SystemData.EnemyId.SLIME)
                    })),
                    new ArrayList<Reward>(Arrays.asList(new Reward[]{
                            new Reward(SystemData.create(SystemData.PotionId.HP), 3, 1),
                            new Reward(SystemData.create(SystemData.PotionId.HP), 1, 0)
                    })),
                    SystemData.create(SystemData.TerrainId.FOREST),
                    500, 5, 2);
        }
    }

    public static class Level_1_2 extends Level
    {
        public Level_1_2() {
            super(SystemData.LevelId.ONE_TWO, "Level 1-2",
                    BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.forest_background),
                    new ArrayList<Enemy>(Arrays.asList(new Enemy[]{
                            SystemData.create(SystemData.EnemyId.SKELETON),
                                    SystemData.create(SystemData.EnemyId.ZOMBIE),
                            SystemData.create(SystemData.EnemyId.SLIME)
                    })),
                    new ArrayList<Reward>(Arrays.asList(new Reward[]{
                            new Reward(SystemData.create(SystemData.PotionId.HP), 3, 1),
                            new Reward(SystemData.create(SystemData.PotionId.HP), 1, 0)
                    })),
                    SystemData.create(SystemData.TerrainId.FOREST),
                    500, 5, 2);
        }
    }
}
