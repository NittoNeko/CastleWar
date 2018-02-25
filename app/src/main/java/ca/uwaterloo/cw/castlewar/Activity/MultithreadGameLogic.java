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
import java.util.HashMap;
import java.util.Random;

import ca.uwaterloo.cw.castlewar.Model.Castle;
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
    public enum Text
    {
        NAME, HP, ATTACK, DEFENSE, SPEED
    }

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
            canvas.drawBitmap(castle.get(true).getPortrait(), castle.get(true).getX(), castle.get(true).getY(), paint);
            canvas.drawBitmap(castle.get(false).getPortrait(), castle.get(false).getX(), castle.get(false).getY(), paint);
            for (Unit unit : unitInCombat.get(true))
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.getX(), unit.getY(), paint);
            }
            for (Unit unit : unitInCombat.get(false))
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
    private HashMap<Boolean, Unit[]> unitInDeck;
    private HashMap<Boolean, Item[]> itemInDeck;
    private HashMap<Boolean, Unit[]> unitInStock;
    private HashMap<Boolean, Item[]> itemInStock;
    private HashMap<Boolean, ArrayList<Unit>> unitInCombat;
    private HashMap<Boolean, Castle> castle;
    private Unit activeUnit;
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
    private State currentState;
    private Random random;
    private boolean isAi;
    private boolean isPlayer1;
    private HashMap<Boolean, Integer> cost;
    private HashMap<Boolean, Integer> maxCost;
    private HashMap<Boolean, Integer> costPerTurn;
    private HashMap<Boolean, Terrain.BattleField> rearBattleField;

    // screen control
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final Handler handler;
    private final ImageView gameScreen;
    private final LinearLayout unitMenu;
    private final LinearLayout combatBoard;
    private HashMap<Text, TextView> attackerInfo;
    private HashMap<Text, TextView> defenderInfo;
    private Canvas canvas;
    private Paint paint;
    private Bitmap screen;

    private MultithreadGameLogic(Activity activity, Handler hander, Terrain terrain)
    {
        this.currentState = State.PREPARE;
        this.activity = activity;
        this.isPlayer1 = true;
        this.unitInCombat.put(true, new ArrayList<Unit>(25));
        this.unitInCombat.put(false, new ArrayList<Unit>(25));
        this.unitInDeck.put(true, new Unit[UNIT_CARD_NUM]);
        this.unitInDeck.put(false, new Unit[UNIT_CARD_NUM]);
        this.itemInDeck.put(true, new Item[ITEM_CARD_NUM]);
        this.itemInDeck.put(false, new Item[ITEM_CARD_NUM]);
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
        this.attackerInfo.put(Text.NAME, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Text.HP, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Text.ATTACK, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Text.DEFENSE, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Text.SPEED, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Text.NAME, (TextView) activity.findViewById(R.id.DefenderName));
        this.attackerInfo.put(Text.HP, (TextView) activity.findViewById(R.id.DefenderHp));
        this.attackerInfo.put(Text.ATTACK, (TextView) activity.findViewById(R.id.DefenderAttack));
        this.attackerInfo.put(Text.DEFENSE, (TextView) activity.findViewById(R.id.DefenderDefense));
        this.attackerInfo.put(Text.SPEED, (TextView) activity.findViewById(R.id.DefenderSpeed));
        this.rearBattleField.put(true, terrain.getBattleFields()[0]);
        this.rearBattleField.put(false, terrain.getBattleFields()[terrain.getBattleFieldNum() - 1]);
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(screen);
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
        this.castle.put(true, SystemData.createCastle(Id.Castle.HOLY.ordinal()));
        this.castle.put(false, SystemData.createCastle(Id.Castle.EVIL.ordinal()));
        this.castle.get(false).setX(backgroundWidth - castle.get(false).getPortrait().getHeight());
        for (int i = 0; i < castle.get(true).getPortrait().getWidth() / Unit.PIXEL; ++i)
            this.rearBattleField.get(true).getTiles()[i].setCastle(castle.get(true));
        for (int i = 0; i < castle.get(false).getPortrait().getWidth() / Unit.PIXEL; ++i)
            this.rearBattleField.get(false).getTiles()[i].setCastle(castle.get(false));
        initializeButtons();
    }

    public MultithreadGameLogic(Activity activity, Handler handler, Level level, Unit[] unitInStockPlayer1, Item[] itemInStockPlayer1) {
        this(activity, handler, level.getTerrain());
        this.isAi = true;
        this.level = level;
        this.unitInStock.put(true, unitInStockPlayer1);
        this.unitInStock.put(false, level.getEnemies());
        this.itemInStock.put(true, itemInStockPlayer1);
        this.itemInDeck.put(false, level.getItems());
        this.cost.put(true, UserProfile.getMaxCost());
        this.cost.put(false, level.getMaxCost());
        this.maxCost.put(true, UserProfile.getMaxCost());
        this.maxCost.put(false, level.getMaxCost());

    }

    public MultithreadGameLogic(Activity activity, Handler handler, Unit[] unitInStockPlayer1, Unit[] unitInStockPlayer2, Terrain terrain) {
        this(activity, handler, terrain);
        this.isAi = true;
        this.terrain = terrain;
    }

    public void generateUnitCard(final ToggleButton toggleButton, int position)
    {
        if (position >= 0 && position < UNIT_CARD_NUM)
        {
            final Unit unit = SystemData.createUnit(unitInStock.get(isPlayer1)[random.nextInt(UNIT_CARD_NUM)].getId());
            unitInDeck.get(isPlayer1)[position] = unit;
            postCard(toggleButton, unit.getCost(), unit.getResource());
        }
    }

    public void generateItemCard(final ToggleButton toggleButton, int position)
    {
        if (position >= 0 && position < UNIT_CARD_NUM) {
            final Item item = SystemData.createItem(itemInStock.get(isPlayer1)[position].getId());
            itemInDeck.get(isPlayer1)[position] = item;
            postCard(toggleButton, item.getCost(), item.getResource());
        }
    }

    public void postCard(final ToggleButton toggleButton, final int cost, final int resource)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toggleButton.setBackgroundResource(resource);
                toggleButton.setTextOff(Integer.toString(cost));
                toggleButton.setTextOn(Integer.toString(cost));
            }
        });
    }

    public void postCost()
    {
        final TextView costView = activity.findViewById(R.id.Cost);
        handler.post(new Runnable() {
            @Override
            public void run() {
                costView.setText(cost.get(isPlayer1) + "/" + maxCost.get(isPlayer1));
            }
        });
    }

    // importatn when switching players
    public void switchPlayer()
    {

    }

    public void initializeButtons()
    {
        final LinearLayout units = activity.findViewById(R.id.Units);
        final LinearLayout items = activity.findViewById(R.id.Items);
        for (int i = 0; i < units.getChildCount(); ++i)
            generateUnitCard((ToggleButton) units.getChildAt(i), i);
        for (int i = 0; i < items.getChildCount(); ++i)
            generateItemCard((ToggleButton) items.getChildAt(i), i);
        Button fightButton = activity.findViewById(R.id.Fight);
        fightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int totalCost = 0;
                        int totalUnit = 0;
                        for (int i = 0; i < units.getChildCount(); ++i)
                            if ( ((ToggleButton) units.getChildAt(i)).isChecked()) {
                                totalCost += unitInDeck.get(isPlayer1)[i].getCost();
                                totalUnit++;
                            }
                        for (int i = 0; i < items.getChildCount(); ++i)
                            if ( ((ToggleButton) items.getChildAt(i)).isChecked()) {
                                totalCost += itemInDeck.get(isPlayer1)[i].getCost();
                            }
                        if (totalCost <= cost.get(isPlayer1) && totalUnit <= rearBattleField.get(isPlayer1).getAvailableTileNum())
                        {
                            for (int i = 0; i < units.getChildCount(); ++i)
                                if ( ((ToggleButton) units.getChildAt(i)).isChecked()) {
                                    Terrain.Tile tile = rearBattleField.get(isPlayer1).findFirstAvailableTile();
                                    Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1)[i].getId());
                                    unit.setX(tile.getX());
                                    unitInCombat.get(isPlayer1).add(unit);
                                    generateUnitCard((ToggleButton) units.getChildAt(i), i);
                                }
                            for (int i = 0; i < items.getChildCount(); ++i)
                                if ( ((ToggleButton) items.getChildAt(i)).isChecked()) {
                                    itemInDeck.get(isPlayer1)[i].use();
                                    generateItemCard((ToggleButton) items.getChildAt(i), i);
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
