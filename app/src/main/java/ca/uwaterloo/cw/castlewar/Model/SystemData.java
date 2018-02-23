package ca.uwaterloo.cw.castlewar.Model;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;


import ca.uwaterloo.cw.castlewar.Activity.MainActivity;
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
        ALLY, ENEMY, POTION, BUFF, LEVEL, TERRAIN, TOWER, FUNCTION
    }
    public enum AllyId
    {
        SWORDMAN, ARCHER, MAGE
    }

    public enum TowerId
    {
        HOLY, EVIL
    }

    public enum PotionId
    {
        HP, ATTACK, DEFENSE, SPEED
    }

    public enum BuffId
    {
        ATTACK, DEFENSE, SPEED
    }

    public enum EnemyId
    {
        SKELETON, ZOMBIE, SLIME
    }

    public enum LevelId
    {
        ONE_ONE,  ONE_TWO
    }

    public enum TerrainId
    {
        FOREST
    }

    // get reference of context
    private static Context context;

    // output control
    private static boolean ifOutput = false;

    // game setting
    public static final int GAME_SPEED = 24;

    // size of the device screen
    private static int screenWidth;
    private static int screenHeight;
    private static int groundLine;

    public static void initializeConfig(Context c, int x, int y)
    {
        context = c;
        screenWidth = x;
        screenHeight = y;
        groundLine = (int) (screenHeight * 1.5);
    }

    public static Ally create(AllyId id)
    {
        if (id == AllyId.SWORDMAN) return new Ally.SwordMan();
        if (id == AllyId.ARCHER) return new Ally.Archer();
        if (id == AllyId.MAGE) return new Ally.Mage();
        else return null;
    }

    public static Enemy create(EnemyId id)
    {
        if (id == EnemyId.SKELETON) return new Enemy.Skeleton();
        if (id == EnemyId.ZOMBIE) return new Enemy.Zombie();
        if (id == EnemyId.SLIME) return new Enemy.Slime();
        else return null;
    }

    public static Potion create(PotionId id)
    {
        if (id == PotionId.HP) return new Potion.HpPotion();
        if (id == PotionId.ATTACK) return new Potion.AttackPotion();
        if (id == PotionId.DEFENSE) return new Potion.DefensePotion();
        if (id == PotionId.SPEED) return new Potion.SpeedPotion();
        else return null;
    }

    public static Buff create(BuffId id)
    {
        if (id == BuffId.ATTACK) return new Buff.AttackBuff();
        if (id == BuffId.DEFENSE) return new Buff.DefenseBuff();
        if (id == BuffId.SPEED) return new Buff.SpeedBuff();
        else return null;
    }
    public static Terrain create(TerrainId id)
    {
        if (id == TerrainId.FOREST) return new Terrain.Forest();
        else return null;
    }

    public static Level create(LevelId id)
    {
        if (id == LevelId.ONE_ONE) return new Level.Level_1_1();
        if (id == LevelId.ONE_TWO) return new Level.Level_1_2();
        else return null;
    }

    private static Bitmap getScaledTerrainBitmap(Resources resources, int source)
    {
        Bitmap original = BitmapFactory.decodeResource(resources, source);
        float ratio = (float) screenHeight / (float) original.getHeight();
        return Bitmap.createScaledBitmap(original, (int) (ratio * (float) original.getWidth()), screenHeight, false);
    }

    public static int getGroundLine()
    {
        return groundLine;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void setIfOutput(boolean ifOutput) {
        SystemData.ifOutput = ifOutput;
    }

    public static boolean isIfOutput() {
        return ifOutput;
    }

    public static Context getContext()
    {
        return context;
    }
}
