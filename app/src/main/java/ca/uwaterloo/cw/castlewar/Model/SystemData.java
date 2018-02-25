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
    // get reference of context
    private static Context context;
    private static BitmapFactory.Options option;

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
        groundLine = (int) (screenHeight * 0.75);
    }

    public static GameObject create(Id id)
    {
        // ALLY
        if (id == Id.SWORDMAN) return new Ally.SwordMan();
        if (id == Id.ARCHER) return new Ally.Archer();
        if (id == Id.MAGE) return new Ally.Mage();

        // ENEMY
        if (id == Id.SKELETON) return new Enemy.Skeleton();
        if (id == Id.ZOMBIE) return new Enemy.Zombie();
        if (id == Id.SLIME) return new Enemy.Slime();

        // POTION
        if (id == Id.HP_POTION) return new Potion.HpPotion();
        if (id == Id.ATTACK_POTION) return new Potion.AttackPotion();
        if (id == Id.DEFENSE_POTION) return new Potion.DefensePotion();
        if (id == Id.SPEED_POTION) return new Potion.SpeedPotion();

        // BUFF
        if (id == Id.ATTACK_BUFF) return new Buff.AttackBuff();
        if (id == Id.DEFENSE_BUFF) return new Buff.DefenseBuff();
        if (id == Id.SPEED_BUFF) return new Buff.SpeedBuff();

        // TERRAIN
        if (id == Id.FOREST) return new Terrain.Forest();

        // LEVEL
        if (id == Id.ONE_ONE) return new Level.Level_1_1();
        if (id == Id.ONE_TWO) return new Level.Level_1_2();
        if (id == Id.ONE_THREE) return new Level.Level_1_1();
        if (id == Id.ONE_FOUR) return new Level.Level_1_2();
        if (id == Id.ONE_FIVE) return new Level.Level_1_1();
        if (id == Id.ONE_SIX) return new Level.Level_1_2();

        // CASTLE
        if (id == Id.HOLY_CASTLE) return new Castle.HolyCastle();
        if (id == Id.EVIL_CASTLE) return new Castle.HolyCastle();

        return null;
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
