package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.Model.Ally;
import ca.uwaterloo.cw.castlewar.Model.Enemy;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harrison33 on 2018/2/19.
 */

public class MultithreadGameLogic {

    // simple condition lock
    public class UiLock extends Object
    {
        public boolean finished = false;
    }

    // thread to update logic
    private class UpdateData extends Thread
    {
        private long realSleepTime;

        private void updateData()
        {

        }

        @Override
        public void run() {
            while (inGame)
            {
                realSleepTime = SystemClock.uptimeMillis() + DATA_SLEEP_TIME;
                updateData();
                realSleepTime -= SystemClock.uptimeMillis();
                if (realSleepTime > 0)
                {
                    try {
                        sleep(realSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // thread to draw into canvas
    // and tell ui thread to update
    private class UpdateScreen extends Thread
    {
        private long realSleepTime;

        private void updateScreen()
        {
            screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(screen);
            canvas.drawBitmap(level.getTerrain().getImage(), 0, 0, paint);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    gameScreen.setImageBitmap(screen);
                    unitMenu.bringToFront();

                    if (SystemData.isIfOutput()) System.out.println("after ui set before wake: " + SystemClock.uptimeMillis());

                    // tell screen thread ui is updated
                    synchronized (uiLock)
                    {
                        uiLock.finished = true;
                        uiLock.notify();
                    }
                }
            });
        }

        @Override
        public void run() {
            while (inGame) {
                realSleepTime = SystemClock.uptimeMillis() + screenSleepTime;
                updateScreen();
                if (SystemData.isIfOutput()) System.out.println("before wait after ui: " + SystemClock.uptimeMillis());
                // wait until ui thread finish its work
                try {
                    synchronized (uiLock)
                    {
                        while (!uiLock.finished) uiLock.wait();
                        uiLock.finished = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (SystemData.isIfOutput()) System.out.println("before time: " + (realSleepTime - screenSleepTime));
                if (SystemData.isIfOutput()) System.out.println("now time: " + SystemClock.uptimeMillis());
                if (SystemData.isIfOutput()) System.out.println("sleep time: " + screenSleepTime);
                realSleepTime -= SystemClock.uptimeMillis();
                if (SystemData.isIfOutput()) System.out.println("real sleep time: " + realSleepTime);
                    if (realSleepTime > 0)
                {
                    // run ahead
                    // let's run faster
                    framePerSecond += 1;
                    if (framePerSecond >= MAX_FPS) framePerSecond = MAX_FPS;
                    screenSleepTime = MILISECOND / (long) framePerSecond;

                    try {
                        sleep(realSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (realSleepTime < 0) {
                    // run below
                    // let's slow down
                    framePerSecond -= 1;
                    if (framePerSecond <= MIN_FPS) framePerSecond = MIN_FPS;
                    screenSleepTime = MILISECOND / (long) framePerSecond;
                }

                if (SystemData.isIfOutput()) System.out.println("FPS: " + framePerSecond);
            }
        }
    }

    // Game object
    private Activity activity;
    private ArrayList<Ally> allies;
    private ArrayList<Enemy> enemies;
    private Level level;

    // game control
    private UpdateData dataThread;
    private UpdateScreen screenThread;
    private final long MILISECOND = 1000;
    private final float DATA_PER_SECOND = SystemData.GAME_SPEED;
    private final long MAX_FPS = 60;
    private final long MIN_FPS = 5;
    private float framePerSecond;
    private long screenSleepTime;
    private final long DATA_SLEEP_TIME = MILISECOND / (long) DATA_PER_SECOND;
    private boolean inGame;
    private UiLock uiLock = new UiLock();

    // screen control
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final Handler handler;
    private final ImageView gameScreen;
    private final LinearLayout unitMenu;
    private final LinearLayout combatBoard;
    private final TextView AttackerName;
    private final TextView AttackerHp;
    private final TextView AttackerAttack;
    private final TextView AttackerDefense;
    private final TextView AttackerSpeed;
    private final TextView DefenderName;
    private final TextView DefenderHp;
    private final TextView DefenderAttack;
    private final TextView DefenderDefense;
    private final TextView DefenderSpeed;
    private Canvas canvas;
    private Paint paint;
    private Bitmap screen;

    public MultithreadGameLogic(Activity activity, Handler hander, Level level) {
        this.allies = new ArrayList<>(25);
        this.enemies = new ArrayList<>(25);
        this.dataThread = null;
        this.screenThread = null;
        this.level = level;
        this.framePerSecond = 30;
        this.paint = new Paint();
        this.inGame = false;
        this.handler = hander;
        this.gameScreen = activity.findViewById(R.id.GameScreen);
        this.unitMenu = activity.findViewById(R.id.UnitMenu);
        this.combatBoard = activity.findViewById(R.id.CombatBoard);
        this.AttackerName = activity.findViewById(R.id.AttackerName);
        this.AttackerHp = activity.findViewById(R.id.AttackerHp);
        this.AttackerAttack = activity.findViewById(R.id.AttackerAttack);
        this.AttackerDefense = activity.findViewById(R.id.AttackerDefense);
        this.AttackerSpeed = activity.findViewById(R.id.AttackerSpeed);
        this.DefenderName = activity.findViewById(R.id.DefenderName);
        this.DefenderHp = activity.findViewById(R.id.DefenderHp);
        this.DefenderAttack = activity.findViewById(R.id.DefenderAttack);
        this.DefenderDefense = activity.findViewById(R.id.DefenderDefense);
        this.DefenderSpeed = activity.findViewById(R.id.DefenderSpeed);
        this.backgroundWidth = level.getTerrain().getImage().getWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
    }

    public void onResume()
    {
        inGame = true;
        dataThread = new UpdateData();
        screenThread = new UpdateScreen();
        dataThread.start();
        screenThread.start();
    }

    public void onPause()
    {
        // wake up all threads
        // inform them to finish work
        inGame = false;
        uiLock.finished = true;
        synchronized (uiLock)
        {
            uiLock.notifyAll();
        }
        if (dataThread != null)
        {
            try {
                dataThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (screenThread != null)
        {
            try {
                screenThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
