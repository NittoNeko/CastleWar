package ca.uwaterloo.cw.castlewar.Model;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.app.WallpaperInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.Random;
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

import static android.graphics.Bitmap.createScaledBitmap;

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


    // output control
    public final static boolean gameFps = false;
    public final static boolean gameFlow = false;

    // game setting
    public static final int CARD_NUM = 5;
    public static final int DRAW_NUM = 2;
    public static final int DRAW_COST = 3;
    public static final long MAX_FPS = 30;
    public static final long MIN_FPS = 5;
    public static final int PIXEL = 100;
    public static final long MILISECOND = 1000;
    public static final float SCROLL_PIXEL_PER_SECOND = 2000; // speed of screen
    public static final int PIXEL_PER_UPDATE = 30;   // speed of character
    public static final long VALUE_PER_SECOND = 25;  // this is speed of text
    public static final float FRAME_PER_SECOND = 30;
    public static final float LOGIC_PER_SECOND = 30;  // this is the speed of updates of normal background thread
    public static final float CONSTANT_PER_SECOND = 10; // this is the speed of updates constant anime, music and so on
    public static final long FRAME_SLEEP_TIME = MILISECOND / (long) FRAME_PER_SECOND;
    public static final long LOGIC_SLEEP_TIME = MILISECOND / (long) LOGIC_PER_SECOND;
    public static final long CONSTANT_SLEEP_TIME = MILISECOND / (long) CONSTANT_PER_SECOND;

    // size of the device screen
    private static int screenWidth;
    private static int screenHeight;
    private static int groundLine;
    private static final int DOWNSIZE = 2;

    // Bitmap preset
    private static Bitmap cross;

    public static void initializeConfig(int x, int y)
    {
        screenWidth = x;
        screenHeight = y;
        groundLine = (int) (screenHeight * 0.8);
        cross = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.cross),PIXEL,PIXEL,false);
    }

    public static Drawable getRandomTitleBackground(){
        Random random = new Random();
        int id = random.nextInt(4);
        switch(id){
            case 0: return scaleDrawable(R.drawable.background_desert, null, groundLine, DOWNSIZE);
            case 1: return scaleDrawable(R.drawable.background_desert_border, null, groundLine, DOWNSIZE);
            case 2: return scaleDrawable(R.drawable.background_desert_road, null, groundLine,DOWNSIZE);
            case 3: return scaleDrawable(R.drawable.background_ruin, null, groundLine,DOWNSIZE);
        }
        return null;
    }

    public static Bitmap getRandomGameBackground(int backgroundWidth){
        Random random = new Random();
        int id = random.nextInt(4);
        switch(id){
            case 0: return scaleBitmap(R.drawable.background_mountain, backgroundWidth, null,DOWNSIZE);
            case 1: return scaleBitmap(R.drawable.background_near_lake, backgroundWidth, null,DOWNSIZE);
            case 2: return scaleBitmap(R.drawable.background_nice_lake, backgroundWidth, null,DOWNSIZE);
            case 3: return scaleBitmap(R.drawable.background_night_forest, backgroundWidth, null,DOWNSIZE);
        }
        return null;
    }

    public static void setContext(Context c){
        context = c;
    }

    public static Unit createUnit(int id) {
        // ALLY
        if (id == Id.Unit.SWORDMAN.ordinal()) return new Ally.SwordMan();
        if (id == Id.Unit.ARCHER.ordinal()) return new Ally.Archer();
        if (id == Id.Unit.MAGE.ordinal()) return new Ally.Mage();

        // ENEMY
        if (id == Id.Unit.BANDIT.ordinal()) return new Enemy.Bandit();
        if (id == Id.Unit.THEIF.ordinal()) return new Enemy.Theif();
        if (id == Id.Unit.RANGER.ordinal()) return new Enemy.Ranger();

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

    public static Bitmap flipHorizontally(int resource, Integer width, Integer height, int downsize){
        Bitmap result = scaleBitmap(resource, width, height, downsize);
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
    }

    public static Drawable scaleDrawable(int resource, Integer width, Integer height, int downsize){
        return new BitmapDrawable(getContext().getResources(),scaleBitmap(resource, width, height, downsize));
    }

    public static Bitmap scaleBitmap(int resource, Integer width, Integer height, int downsize){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = downsize;
        if (width == null && height == null) {
            return BitmapFactory.decodeResource(context.getResources(), resource, option);
        } else if (width == null){
            Bitmap original = BitmapFactory.decodeResource(context.getResources(), resource, option);
            float ratio = (float) height / (float) original.getHeight();
            return Bitmap.createScaledBitmap(original, (int) (original.getWidth() * ratio), height, true);
        } else if (height == null){
            Bitmap original = BitmapFactory.decodeResource(context.getResources(), resource, option);
            float ratio = (float) width / (float)original.getWidth();
            return Bitmap.createScaledBitmap(original, width,  (int) (original.getHeight() * ratio), true);
        }

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resource, option), width, height, true);
    }

    public static Bitmap scaleIconBitmap(int resource, int downsize) {
        return scaleBitmap(resource, PIXEL, PIXEL, downsize);
    }

    public static Bitmap scaleIconBitmap(Bitmap bitmap)
    {
        return Bitmap.createScaledBitmap(bitmap, PIXEL, PIXEL, true);
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

    public static Context getContext()
    {
        return context;
    }
}
