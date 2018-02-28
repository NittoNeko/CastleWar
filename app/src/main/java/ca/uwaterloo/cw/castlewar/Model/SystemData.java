package ca.uwaterloo.cw.castlewar.Model;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Point;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Handler;


import ca.uwaterloo.cw.castlewar.Activity.MainActivity;
import ca.uwaterloo.cw.castlewar.Activity.MultithreadGameLogic;
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
    public static final ExecutorService gameThreads = Executors.newFixedThreadPool(3);
    public static final ExecutorService oneTimeThread = Executors.newFixedThreadPool(5);
    private static android.os.Handler handler = new android.os.Handler();
    private static Bitmap cross;
    private static TextView inventoryCoin;
    private static TextView shopCoin;

    // output control
    private static boolean ifOutput = false;

    // game setting
    public final static int PIXEL = 100;

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
        cross = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.cross),PIXEL,PIXEL,false);
    }

    public static Unit createUnit(int id) {
        // ALLY
        if (id == Id.Unit.SWORDMAN.ordinal()) return new Ally.SwordMan();
        if (id == Id.Unit.ARCHER.ordinal()) return new Ally.Archer();
        if (id == Id.Unit.MAGE.ordinal()) return new Ally.Mage();

        // ENEMY
        if (id == Id.Unit.SKELETON.ordinal()) return new Enemy.Skeleton();
        if (id == Id.Unit.ZOMBIE.ordinal()) return new Enemy.Zombie();
        if (id == Id.Unit.SLIME.ordinal()) return new Enemy.Slime();

        // CASTLE
        if (id == Id.Unit.HOLY_CASTLE.ordinal()) return new Castle.HolyCastle();
        if (id == Id.Unit.EVIL_CASTLE.ordinal()) return new Castle.HolyCastle();
        return null;
    }

    public static Item createItem(int id)
    {
        // POTION
        if (id == Id.Item.HP_POTION.ordinal()) return new Potion.HpPotion();
        if (id == Id.Item.ATTACK_POTION.ordinal()) return new Potion.AttackPotion();
        if (id == Id.Item.DEFENSE_POTION.ordinal()) return new Potion.DefensePotion();
        if (id == Id.Item.SPEED_POTION.ordinal()) return new Potion.SpeedPotion();
        return null;
    }

    public static Buff createBuff(int id)
    {
        // BUFF
        if (id == Id.Buff.ATTACK.ordinal()) return new Buff.AttackBuff();
        if (id == Id.Buff.DEFENSE.ordinal()) return new Buff.DefenseBuff();
        if (id == Id.Buff.SPEED.ordinal()) return new Buff.SpeedBuff();
        return null;
    }

    public static Terrain createTerrain(int id)
    {
        // TERRAIN
        if (id == Id.Terrain.FOREST.ordinal()) return new Terrain.Forest();
        return null;
    }

    public static Level createLevel(int id)
    {
        // LEVEL
        if (id == Id.Level.ONE_ONE.ordinal()) return new Level.Level_1_1();
        if (id == Id.Level.ONE_TWO.ordinal()) return new Level.Level_1_2();
        if (id == Id.Level.ONE_THREE.ordinal()) return new Level.Level_1_1();
        if (id == Id.Level.ONE_FOUR.ordinal()) return new Level.Level_1_2();
        if (id == Id.Level.ONE_FIVE.ordinal()) return new Level.Level_1_1();
        if (id == Id.Level.ONE_SIX.ordinal()) return new Level.Level_1_2();
        return null;
    }

    public static Bitmap scaleIconBitmap(Bitmap bitmap)
    {
        return Bitmap.createScaledBitmap(bitmap, PIXEL, PIXEL, false);
    }

    public static TextView getShopCoin() {
        return shopCoin;
    }

    public static void setShopCoin(TextView shopCoin) {
        SystemData.shopCoin = shopCoin;
    }

    public static TextView getInventoryCoin() {
        return inventoryCoin;
    }

    public static void setInventoryCoin(TextView inventoryCoin) {
        SystemData.inventoryCoin = inventoryCoin;
    }
    public static Bitmap getEmptyIcon(){
        return cross;
    }

    public static void postToUi(Runnable r){
        handler.post(r);
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
