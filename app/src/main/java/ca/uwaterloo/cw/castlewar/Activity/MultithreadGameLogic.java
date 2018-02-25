package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Random;

import ca.uwaterloo.cw.castlewar.Model.Ally;
import ca.uwaterloo.cw.castlewar.Model.Castle;
import ca.uwaterloo.cw.castlewar.Model.CombatObject;
import ca.uwaterloo.cw.castlewar.Model.Enemy;
import ca.uwaterloo.cw.castlewar.Model.GameObject;
import ca.uwaterloo.cw.castlewar.Model.Id;
import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.Model.Target;
import ca.uwaterloo.cw.castlewar.Model.Terrain;
import ca.uwaterloo.cw.castlewar.Model.Unit;
import ca.uwaterloo.cw.castlewar.Model.UserProfile;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harrison33 on 2018/2/19.
 */

public class MultithreadGameLogic {

    public enum State
    {
        PREPARE, MOVING, BATTLING
    }

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
            // check if new units are needed
            for (Unit unit : unitInCombatPlayer1)
            {
                unit.update();
            }
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
            canvas.drawBitmap(level.getPortrait(), level.getX(), level.getY(), paint);
            canvas.drawBitmap(level.getTerrain().getPortrait(), level.getTerrain().getX(), level.getTerrain().getY(), paint);
            canvas.drawBitmap(leftCastle.getMovingImage(), leftCastle.getX(), leftCastle.getY(), paint);
            canvas.drawBitmap(rightCastle.getMovingImage(), rightCastle.getX(), rightCastle.getY(), paint);
            for (Unit unit : unitInCombatPlayer1)
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.getX(), unit.getY(), paint);
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    gameScreen.setImageBitmap(screen);
                    if (currentState == MultithreadGameLogic.State.PREPARE)
                    {
                        unitMenu.bringToFront();
                    } else if (currentState == MultithreadGameLogic.State.MOVING)
                    {
                        gameScreen.bringToFront();
                    } else if (currentState == MultithreadGameLogic.State.BATTLING)
                    {
                        combatBoard.bringToFront();
                    }

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
    private final int UNIT_CARD_NUM = 5;
    private final int ITEM_CARD_NUM = 5;
    private Activity activity;
    private Unit[] unitInDeckPlayer1;
    private Unit[] unitInDeckPlayer2;
    private Item[] itemInDeckPlayer1;
    private Item[] itemInDeckPlayer2;
    private Unit[] unitInStockPlayer1;
    private Unit[] unitInStockPlayer2;
    private Item[] itemInStockPlayer1;
    private Item[] itemInStockPlayer2;
    private ArrayList<Unit> unitInCombatPlayer1;
    private ArrayList<Unit> unitInCombatPlayer2;
    private Castle leftCastle;
    private Castle rightCastle;
    private Target target;
    private Level level;
    private Terrain terrain;

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
    private UiLock uiLock;
    private State currentState = State.PREPARE;
    private Random random;
    private boolean isAi;
    private boolean isPlayer1;
    private int costPlayer1;
    private int maxCostPlayer1;
    private int costPlayer2;
    private int maxCostPlayer2;
    private int costRecoverySpeedPlayer1;
    private int costRecoverySpeedPlayer2;
    private Terrain.BattleField leftBattleField;
    private Terrain.BattleField rightBattleField;

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

    private MultithreadGameLogic(Activity activity, Handler hander, Terrain terrain)
    {
        this.activity = activity;
        this.isPlayer1 = true;
        this.unitInCombatPlayer1 = new ArrayList<>(25);
        this.unitInCombatPlayer2 = new ArrayList<>(25);
        this.unitInDeckPlayer1 = new Unit[UNIT_CARD_NUM];
        this.unitInDeckPlayer2 = new Unit[UNIT_CARD_NUM];
        this.itemInDeckPlayer1 = new Item[ITEM_CARD_NUM];
        this.itemInDeckPlayer2 = new Item[ITEM_CARD_NUM];
        this.dataThread = null;
        this.screenThread = null;
        this.framePerSecond = 30;
        this.paint = new Paint();
        this.inGame = false;
        this.handler = hander;
        this.random = new Random();
        this.uiLock = new UiLock();
        this.terrain = terrain;
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
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(screen);
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
        this.leftCastle = (Castle) SystemData.create(Id.HOLY_CASTLE);
        this.rightCastle = (Castle) SystemData.create(Id.EVIL_CASTLE);
        this.rightCastle.setX(backgroundWidth - rightCastle.getMovingImage().getHeight());
        for (int i = 0; i < leftCastle.getMovingImage().getWidth() / Ally.PIXEL; ++i)
            this.terrain.getBattleFields()[0].getTiles()[i].setUnit(leftCastle);
        for (int i = 0; i < rightCastle.getMovingImage().getWidth() / Ally.PIXEL; ++i)
            this.terrain.getBattleFields()[terrain.getBattleFieldNum() - 1].getTiles()[i].setUnit(rightCastle);
        this.leftBattleField = terrain.getBattleFields()[0];
        this.rightBattleField = terrain.getBattleFields()[terrain.getBattleFieldNum() - 1];
        initializeButtons();
    }

    public MultithreadGameLogic(Activity activity, Handler handler, Level level, Unit[] unitInStockPlayer1, Item[] itemInStockPlayer1) {
        this(activity, handler, level.getTerrain());
        this.isAi = true;
        this.level = level;
        this.unitInStockPlayer1 = unitInStockPlayer1;
        this.unitInStockPlayer2 = level.getEnemies();
        this.itemInStockPlayer1 = itemInStockPlayer1;
        this.itemInStockPlayer2 = level.getItems();
        this.maxCostPlayer1 = UserProfile.getMaxCost();
        this.costPlayer1 = maxCostPlayer1;
        this.maxCostPlayer2 = level.getMaxCost();
        this.costPlayer2 = maxCostPlayer2;
    }

    public MultithreadGameLogic(Activity activity, Handler handler, Unit[] unitInStockPlayer1, Unit[] unitInStockPlayer2, Terrain terrain) {
        this(activity, handler, terrain);
        this.isAi = true;
        this.terrain = terrain;
        this.unitInStockPlayer1 = unitInStockPlayer1;
        this.unitInStockPlayer2 = unitInStockPlayer2;
    }

    public void generateUnitCard(final ToggleButton toggleButton, int position)
    {
        if (position >= 0 && position < UNIT_CARD_NUM)
        {
            if (isPlayer1)
            {
                final Unit unit = (Unit) SystemData.create(unitInStockPlayer1[random.nextInt(UNIT_CARD_NUM)].getId());
                unitInDeckPlayer1[position] = unit;
                postCard(toggleButton, unit);
            } else
            {
                final Unit unit = (Unit) SystemData.create(unitInStockPlayer2[random.nextInt(UNIT_CARD_NUM)].getId());
                unitInDeckPlayer2[position] = unit;
                postCard(toggleButton, unit);
            }
        }
    }

    public void generateItemCard(final ToggleButton toggleButton, int position)
    {
        if (position >= 0 && position < UNIT_CARD_NUM)
        {
            if (isPlayer1)
            {
                final Item item = (Item) SystemData.create(itemInStockPlayer1[position].getId());
                itemInDeckPlayer1[position] = item;
                postCard(toggleButton, item);
            } else
            {
                final Item item = (Item) SystemData.create(itemInStockPlayer2[position].getId());
                itemInDeckPlayer1[position] = item;
                postCard(toggleButton, item);
            }
        }
    }

    public void postCard(final ToggleButton toggleButton, final CombatObject unit)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toggleButton.setBackgroundResource(unit.getResource());
                toggleButton.setTextOff(Integer.toString(unit.getCost()));
                toggleButton.setTextOn(Integer.toString(unit.getCost()));
            }
        });
    }

    public void initializeButtons()
    {
        final LinearLayout units = activity.findViewById(R.id.Units);
        final LinearLayout items = activity.findViewById(R.id.Items);
        for (int i = 0; i < units.getChildCount(); ++i)
            if ( ((ToggleButton) units.getChildAt(i)).isChecked()) {

            }
        Button fightButton = activity.findViewById(R.id.Fight);
        fightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int totalCost = 0;
                        int totalUnit = 0;
                        if (isPlayer1)
                        {
                            for (int i = 0; i < units.getChildCount(); ++i)
                                if ( ((ToggleButton) units.getChildAt(i)).isChecked()) {
                                    totalCost += unitInDeckPlayer1[i].getCost();
                                    totalUnit++;
                                }
                            for (int i = 0; i < items.getChildCount(); ++i)
                                if ( ((ToggleButton) items.getChildAt(i)).isChecked()) {
                                    totalCost += itemInDeckPlayer1[i].getCost();
                                }
                            if (totalCost <= costPlayer1 && totalUnit <= leftBattleField.getAvailableTileNum())
                            {
                                for (int i = 0; i < units.getChildCount(); ++i)
                                    if ( ((ToggleButton) units.getChildAt(i)).isChecked()) {
                                        Terrain.Tile tile = leftBattleField.findFirstAvailableTile();
                                        Unit unit = (Unit) SystemData.create(unitInDeckPlayer1[i].getId());
                                        unit.setX(tile.getX());
                                        unitInCombatPlayer1.add(unit);
                                        generateUnitCard((ToggleButton) units.getChildAt(i), i);
                                    }
                                for (int i = 0; i < items.getChildCount(); ++i)
                                    if ( ((ToggleButton) items.getChildAt(i)).isChecked()) {
                                        // use item not now
                                    }
                            }
                        }
                    }
                }).start();
            }
        });
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
