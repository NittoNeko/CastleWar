package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

/*
    Here are basic mappings from id to gameobject
    and utility functions
*/

public class SystemData {
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

    public enum TypeId
    {
        ALLY(0), ENEMY(1), POTION(2), TOWER(3), BUFF(4), LEVEL(5), TERRAIN(6);

        private final int id;
        private TypeId(int id) {this.id = id;}
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
    public static final int LEVEL_NUM = 1;
    public static final int TERRAIN_NUM = 1;

    // Samples of every Game object
    private static final ArrayList<Level> LEVELS = new ArrayList<>(LEVEL_NUM);
    private static final ArrayList<Ally> ALLIES = new ArrayList<>(ALLY_NUM);
    private static final ArrayList<Enemy> ENEMIES = new ArrayList<>(ENEMY_NUM);
    private static final ArrayList<Potion> POTIONS = new ArrayList<>(POTION_NUM );
    private static final ArrayList<Tower> TOWERS = new ArrayList<>(TOWER_NUM );
    private static final ArrayList<Buff> BUFFS = new ArrayList<>(BUFF_NUM);
    private static final ArrayList<Terrain> TERRAINS = new ArrayList<>(TERRAIN_NUM);

    // bitmap of game object
    private static final ArrayList<Bitmap> ALLY_BITMAP = new ArrayList<>(ALLY_NUM );
    private static final ArrayList<Bitmap> ENEMY_BITMAP = new ArrayList<>(ENEMY_NUM);
    private static final ArrayList<Bitmap> POTION_BITMAP = new ArrayList<>(POTION_NUM );
    private static final ArrayList<Bitmap> TOWER_BITMAP = new ArrayList<>(TOWER_NUM );
    private static final ArrayList<Bitmap> BUFF_BITMAP = new ArrayList<>(BUFF_NUM);
    private static final ArrayList<Bitmap> TERRAIN_BITMAP = new ArrayList<>(TERRAIN_NUM);

    public static void initializeData()
    {
        // Sample Ally
        ALLIES.set(AllyId.SWORDMAN.id(), new Ally.SwordMan());
        ALLIES.set(AllyId.ARCHER.id(), new Ally.Archer());
        ALLIES.set(AllyId.MAGE.id(), new Ally.Mage());

        // Sample Enemy
        ENEMIES.set(EnemyId.SKELETON.id(), new Enemy.Skeleton());
        ENEMIES.set(EnemyId.ZOMBIE.id(), new Enemy.Zombie());
        ENEMIES.set(EnemyId.SLIME.id(), new Enemy.Skeleton());

        // Sample Potion
        POTIONS.set(PotionId.HP.id(), new Potion.HpPotion());
        POTIONS.set(PotionId.ATTACK.id(), new Potion.AttackPotion());
        POTIONS.set(PotionId.DEFENSE.id(), new Potion.DefensePotion());
        POTIONS.set(PotionId.SPEED.id(), new Potion.SpeedPotion());

        // Sample Buff
        BUFFS.set(BuffId.ATTACK.id(), new Buff.AttackBuff());
        BUFFS.set(BuffId.DEFENSE.id(), new Buff.DefenseBuff());
        BUFFS.set(BuffId.SPEED.id(), new Buff.SpeedBuff());

        // Sample Level
        LEVELS.set(LevelId.ONE_ONE.id(), new Level.Level_1_1());
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
        if (type == TypeId.ALLY.id()) return ALLIES.get(id);
        else if (type == TypeId.ENEMY.id()) return ENEMIES.get(id);
        else if (type == TypeId.POTION.id()) return POTIONS.get(id);
        else if (type == TypeId.BUFF.id()) return BUFFS.get(id);
        else if (type == TypeId.LEVEL.id()) return LEVELS.get(id);
        else if (type == TypeId.TERRAIN.id()) return TERRAINS.get(id);
        return null;
    }

    // convert Drawable to Bitmap
    public static void initializeBitmap(Context context)
    {
        ALLY_BITMAP.set(AllyId.SWORDMAN.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        ALLY_BITMAP.set(AllyId.ARCHER.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        ALLY_BITMAP.set(AllyId.MAGE.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));

        ENEMY_BITMAP.set(EnemyId.SKELETON.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        ENEMY_BITMAP.set(EnemyId.ZOMBIE.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        ENEMY_BITMAP.set(EnemyId.SLIME.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));

        POTION_BITMAP.set(PotionId.HP.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        POTION_BITMAP.set(PotionId.ATTACK.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        POTION_BITMAP.set(PotionId.DEFENSE.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        POTION_BITMAP.set(PotionId.SPEED.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));

        TOWER_BITMAP.set(TowerId.EVIL.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        TOWER_BITMAP.set(TowerId.HOLY.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));

        BUFF_BITMAP.set(BuffId.ATTACK.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        BUFF_BITMAP.set(BuffId.DEFENSE.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        BUFF_BITMAP.set(BuffId.SPEED.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));

        TERRAIN_BITMAP.set(TerrainId.FOREST.id(), BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
    }

    public static int getSize()
    {
        return ALLY_BITMAP.size();
    }

    public static Level getLevel(int position)
    {
        return LEVELS.get(position);
    }

    public static Bitmap getAllyBitmap(int id) {return id < ALLY_BITMAP.size() ? ALLY_BITMAP.get(id) : null;}

    public static Bitmap getEnemyBitmap(int id) {return id < ENEMY_BITMAP.size() ? ENEMY_BITMAP.get(id) : null;}

    public static Bitmap getPotionBitmap(int id) {return id < POTION_BITMAP.size() ? POTION_BITMAP.get(id) : null;}

    public static Bitmap getTowerBitmap(int id) {return id < TOWER_BITMAP.size() ? TOWER_BITMAP.get(id) : null;}

    public static Bitmap getBuffBitmap(int id) {return id < BUFF_BITMAP.size() ? BUFF_BITMAP.get(id) : null;}
}
