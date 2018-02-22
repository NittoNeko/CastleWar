package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.Model.Ally;
import ca.uwaterloo.cw.castlewar.Model.Enemy;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.SystemData;

/**
 * Created by harrison33 on 2018/2/19.
 */

public class GameLogic implements Runnable {
    // Game object
    private ArrayList<Ally> allies;
    private ArrayList<Enemy> enemies;
    private Level level;

    // game control
    private Activity activity;
    private Thread gameThread;
    private long framePerSecond;
    private long sleepTime;
    private long elapsedTime;
    private boolean inGame;
    private final long MILISECOND = 1000;
    private final long MAX_FPS = 60;
    private final long MIN_FPS = 5;

    // screen control
    private final Handler handler;
    private final ImageView imageView;
    private Canvas canvas;
    private Paint paint;

    public GameLogic(Activity activity, Handler handler, ImageView imageView, Level level) {
        this.allies = new ArrayList<>(25);
        this.enemies = new ArrayList<>(25);
        this.gameThread = null;
        this.level = level;
        this.framePerSecond = 30;
        this.sleepTime = MILISECOND / framePerSecond;
        this.paint = new Paint();
        this.inGame = false;
        this.imageView = imageView;
        this.handler = handler;
        this.activity = activity;

    }

    public void updateGameData()
    {

    }

    public void updateScreen()
    {
        final Bitmap screen = Bitmap.createBitmap(level.getTerrain().getImage().getWidth(), SystemData.getScreenHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(screen);
        canvas.drawBitmap(level.getTerrain().getImage(), 0, 0, paint);
        handler.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(screen);
            }
        });

    }

    public void run() {
        while (inGame) {
            // update game data is much faster than update screens
            // so let it be consistent with frame per second
            updateGameData();

            // update screen could be slow
            if (SystemClock.elapsedRealtime() <= elapsedTime + sleepTime) {
                // run ahead
                // let's run faster
                framePerSecond += 1;
                if (framePerSecond >= MAX_FPS) framePerSecond = MAX_FPS;
            } else {

            }
            updateScreen();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onResume()
    {
        inGame = true;
        elapsedTime = SystemClock.elapsedRealtime();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void onPause()
    {
        inGame = false;
        if (gameThread != null)
        {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
