package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.Activity.GameLogic;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

/*
    Here are basic mappings from id to gameobject
    and utility functions
*/

public class SystemData {
    public enum TypeId
    {
        ALLY(0), ENEMY(1), POTION(2), TOWER(3), BUFF(4), LEVEL(5), TERRAIN(6);

        private final int id;
        private TypeId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum AllyId
    {
        SWORDMAN(0), ARCHER(1), MAGE(2);

        private final int id;
        private AllyId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum TowerId
    {
        HOLY(0), EVIL(1);

        private final int id;
        private TowerId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum PotionId
    {
        HP(0), ATTACK(1), DEFENSE(2), SPEED(3);

        private final int id;
        private PotionId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum BuffId
    {
        ATTACK(0), DEFENSE(1), SPEED(2);

        private final int id;
        private BuffId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum EnemyId
    {
        SKELETON(0), ZOMBIE(1), SLIME(2);

        private final int id;
        private EnemyId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum LevelId
    {
        ONE_ONE(0), ONE_TWO(1), ONE_THREE(2), ONE_FOUR(3), ONE_FIVE(4), ONE_SIX(5);

        private final int id;
        private LevelId(int id) {this.id = id;}
        public int id() {return id;}
    }

    public enum TerrainId
    {
        FOREST(0);

        private final int id;
        private TerrainId(int id) {this.id = id;}
        public int id() {return id;}
    }
    // total num of types of gameobjects
    public static final int ALLY_NUM = 3;
    public static final int ENEMY_NUM = 3;
    public static final int POTION_NUM = 4;
    public static final int TOWER_NUM = 2;
    public static final int BUFF_NUM = 3;
    public static final int LEVEL_NUM = 2;
    public static final int TERRAIN_NUM = 1;

    // Samples of every Game object
    private static final Level[] LEVELS = new Level[LEVEL_NUM];
    private static final Ally[] ALLIES = new Ally[ALLY_NUM];
    private static final Enemy[] ENEMIES = new Enemy[ENEMY_NUM];
    private static final Potion[] POTIONS = new Potion[POTION_NUM];
    private static final Buff[] BUFFS = new Buff[BUFF_NUM];
    private static final Terrain[] TERRAINS = new Terrain[TERRAIN_NUM];
    private static final Tower[] TOWERS = new Tower[TOWER_NUM];

    // bitmap of game object
    private static final Bitmap[] ALLY_BITMAPS = new Bitmap[ALLY_NUM];
    private static final Bitmap[] ENEMY_BITMAPS = new Bitmap[ENEMY_NUM];
    private static final Bitmap[] POTION_BITMAPS = new Bitmap[POTION_NUM];
    private static final Bitmap[] BUFF_BITMAPS = new Bitmap[BUFF_NUM];
    private static final Bitmap[] TERRAIN_BITMAPS = new Bitmap[TERRAIN_NUM];
    private static final Bitmap[] TOWER_BITMAPS = new Bitmap[TOWER_NUM];

    // Initialize every game object
    // Pass only references to save CPU
    // Order of Type being initialized MATTERS!
    // Buff > Potion
    // Terrain > Level
    public static void initializeData()
    {
        // Sample Ally
        ALLIES[AllyId.SWORDMAN.id()] = new Ally.SwordMan();
        ALLIES[AllyId.ARCHER.id()] = new Ally.Archer();
        ALLIES[AllyId.MAGE.id()] = new Ally.Mage();

        // Sample Enemy
        ENEMIES[EnemyId.SKELETON.id()] = new Enemy.Skeleton();
        ENEMIES[EnemyId.ZOMBIE.id()] = new Enemy.Zombie();
        ENEMIES[EnemyId.SLIME.id()] = new Enemy.Skeleton();

        // Sample Buff
        BUFFS[BuffId.ATTACK.id()] = new Buff.AttackBuff();
        BUFFS[BuffId.DEFENSE.id()] = new Buff.DefenseBuff();
        BUFFS[BuffId.SPEED.id()] = new Buff.SpeedBuff();

        // Sample Potion
        POTIONS[PotionId.HP.id()] = new Potion.HpPotion();
        POTIONS[PotionId.ATTACK.id()] = new Potion.AttackPotion();
        POTIONS[PotionId.DEFENSE.id()] = new Potion.DefensePotion();
        POTIONS[PotionId.SPEED.id()] = new Potion.SpeedPotion();

        // Sample Terrain
        TERRAINS[TerrainId.FOREST.id()] = new Terrain.Forest();

        // Sample Level
        LEVELS[LevelId.ONE_ONE.id()] = new Level.Level_1_1();
        LEVELS[LevelId.ONE_TWO.id()] = new Level.Level_1_2();
    }

    // create any game object
    // need to cast to subclasses
    public static GameObject create(int id, int type)
    {
        if (type == TypeId.ALLY.id())
        {
            if (id == AllyId.SWORDMAN.id()) return new Ally.SwordMan();
            if (id == AllyId.ARCHER.id()) return new Ally.Archer();
            if (id == AllyId.MAGE.id()) return new Ally.Mage();
        } else if (type == TypeId.ENEMY.id())
        {
            if (id == EnemyId.SKELETON.id()) return new Enemy.Skeleton();
            if (id == EnemyId.ZOMBIE.id()) return new Enemy.Zombie();
            if (id == EnemyId.SLIME.id()) return new Enemy.Slime();
        } else if (type == TypeId.POTION.id())
        {
            if (id == PotionId.HP.id()) return new Potion.HpPotion();
            if (id == PotionId.ATTACK.id()) return new Potion.AttackPotion();
            if (id == PotionId.DEFENSE.id()) return new Potion.DefensePotion();
            if (id == PotionId.SPEED.id()) return new Potion.SpeedPotion();
        } else if (type == TypeId.BUFF.id())
        {
            if (id == BuffId.ATTACK.id()) return new Buff.AttackBuff();
            if (id == BuffId.DEFENSE.id()) return new Buff.DefenseBuff();
            if (id == BuffId.SPEED.id()) return new Buff.SpeedBuff();
        } else if (type == TypeId.TERRAIN.id())
        {
            if (id == TerrainId.FOREST.id()) return new Terrain.Forest();
        }
        return null;
    }

    // get sample game object
    // need to cast to subclasses
    public static GameObject get(int id, int type)
    {
        if (type == TypeId.ALLY.id()) return ALLIES[id];
        else if (type == TypeId.ENEMY.id()) return ENEMIES[id];
        else if (type == TypeId.POTION.id()) return POTIONS[id];
        else if (type == TypeId.BUFF.id()) return BUFFS[id];
        else if (type == TypeId.LEVEL.id()) return LEVELS[id];
        else if (type == TypeId.TERRAIN.id()) return TERRAINS[id];
        return null;
    }

    // convert Drawable to Bitmap
    public static void initializeBitmap(Context context)
    {
        ALLY_BITMAPS[AllyId.SWORDMAN.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        ALLY_BITMAPS[AllyId.ARCHER.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        ALLY_BITMAPS[AllyId.MAGE.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);

        ENEMY_BITMAPS[EnemyId.SKELETON.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        ENEMY_BITMAPS[EnemyId.ZOMBIE.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        ENEMY_BITMAPS[EnemyId.SLIME.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);

        POTION_BITMAPS[PotionId.HP.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        POTION_BITMAPS[PotionId.ATTACK.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        POTION_BITMAPS[PotionId.DEFENSE.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        POTION_BITMAPS[PotionId.SPEED.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);

        TOWER_BITMAPS[TowerId.EVIL.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        TOWER_BITMAPS[TowerId.HOLY.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);

        BUFF_BITMAPS[BuffId.ATTACK.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        BUFF_BITMAPS[BuffId.DEFENSE.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        BUFF_BITMAPS[BuffId.SPEED.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);

        TERRAIN_BITMAPS[TerrainId.FOREST.id()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
    }

    public static void startLevel(Context context, int levelId)
    {
        GameLogic game = new GameLogic(context, SystemData.getLevel(levelId));
    }

    public static Level getLevel(int position)
    {
        return LEVELS[position];
    }

    public static Bitmap getAllyBitmap(int id) {return id < ALLY_NUM ? ALLY_BITMAPS[id] : null;}

    public static Bitmap getEnemyBitmap(int id) {return id < ENEMY_NUM ? ENEMY_BITMAPS[id] : null;}

    public static Bitmap getPotionBitmap(int id) {return id < POTION_NUM ? POTION_BITMAPS[id] : null;}

    public static Bitmap getTowerBitmap(int id) {return id < TOWER_NUM ? TOWER_BITMAPS[id] : null;}

    public static Bitmap getBuffBitmap(int id) {return id < BUFF_NUM ? BUFF_BITMAPS[id] : null;}
}
