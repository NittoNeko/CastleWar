package ca.uwaterloo.cw.castlewar.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    private ArrayList<Enemy> enemies;
    private ArrayList<Reward> rewards;
    private Terrain terrain;
    private int coinRewards;
    private int areas;
    private int maxCost;

    public Level(int id, String name, ArrayList<Enemy> enemies, ArrayList<Reward> rewards, Terrain terrain, int coinRewards, int areas, int maxCost)
    {
        super(id, name, SystemData.TypeId.LEVEL.id());
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
        return displayableEnemies + "two zombies are coming at you, and you may be very scary";
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

    public Terrain getTerrain() {
        return terrain;
    }

    public static class Level_1_1 extends Level
    {
        public Level_1_1() {
            super(SystemData.LevelId.ONE_ONE.id(), "Level 1-1",
                    new ArrayList<Enemy>(Arrays.asList(new Enemy[]{
                            (Enemy) SystemData.get(SystemData.EnemyId.SKELETON.id(), SystemData.TypeId.ENEMY.id()),
                            (Enemy) SystemData.get(SystemData.EnemyId.ZOMBIE.id(), SystemData.TypeId.ENEMY.id()),
                            (Enemy) SystemData.get(SystemData.EnemyId.SLIME.id(), SystemData.TypeId.ENEMY.id())
                    })),
                    new ArrayList<Reward>(Arrays.asList(new Reward[]{
                            new Reward((Potion) SystemData.get(SystemData.PotionId.HP.id(), SystemData.TypeId.POTION.id()), 3, 1),
                            new Reward((Potion) SystemData.get(SystemData.PotionId.HP.id(), SystemData.TypeId.POTION.id()), 1, 0)
                    })),
                    (Terrain) SystemData.get(SystemData.TerrainId.FOREST.id(), SystemData.TypeId.TERRAIN.id()),
                    500, 5, 2);
        }
    }

    public static class Level_1_2 extends Level
    {
        public Level_1_2() {
            super(SystemData.LevelId.ONE_TWO.id(), "Level 1-2",
                    new ArrayList<Enemy>(Arrays.asList(new Enemy[]{
                            (Enemy) SystemData.get(SystemData.EnemyId.SKELETON.id(), SystemData.TypeId.ENEMY.id()),
                            (Enemy) SystemData.get(SystemData.EnemyId.ZOMBIE.id(), SystemData.TypeId.ENEMY.id()),
                            (Enemy) SystemData.get(SystemData.EnemyId.SLIME.id(), SystemData.TypeId.ENEMY.id())
                    })),
                    new ArrayList<Reward>(Arrays.asList(new Reward[]{
                            new Reward((Potion) SystemData.get(SystemData.PotionId.HP.id(), SystemData.TypeId.POTION.id()), 3, 1),
                            new Reward((Potion) SystemData.get(SystemData.PotionId.HP.id(), SystemData.TypeId.POTION.id()), 1, 0)
                    })),
                    (Terrain) SystemData.get(SystemData.TerrainId.FOREST.id(), SystemData.TypeId.TERRAIN.id()),
                    500, 5, 2);
        }
    }
}
