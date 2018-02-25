package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private enum StateId
    {
        PREPARE, MOVING, COMBAT
    }

    public enum ThreadId
    {
        DATA, SCREEN, ANIME
    }

    public enum TextId
    {
        NAME, HP, ATTACK, DEFENSE, SPEED
    }

    public class AtomicState
    {
        private StateId state;
        synchronized StateId get()
        {
            return state;
        }
        synchronized void set(StateId state)
        {
            this.state = state;
        }
    }

    // simple condition lock
    public class ScreenCondition extends Object
    {
        public boolean finished = false;
    }

    public class AnimeCondition extends Object
    {
        public boolean hasUserInput = false;
    }

    // thread to update logic
    private class UpdateData implements Runnable
    {

        private void updateData()
        {
            synchronized (animeCondition)
            {
                while(!animeCondition.hasUserInput)
                    try {
                        animeCondition.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

            // move units by its adding ordering
            unitInCombatLock.readLock().lock();
            for (Unit unit : unitInCombat.get(isPlayer1))
            {
                unit.decideStrategy(terrain);
            }

            // if Ai
            if (isAi)
            {

            } else switchPlayer();

            currentState.set(StateId.PREPARE);
        }

        @Override
        public void run() {
            while (isGameActive.get())
            {
                updateData();
            }
        }
    }

    // update constant animation
    private class UpdateAnime implements Runnable
    {
        private long realSleepTime;
        public void updateAnime()
        {
            unitInCombatLock.readLock().lock();
            for (Unit unit : unitInCombat.get(true))
            {
                unit.animate();
            }
            for (Unit unit : unitInCombat.get(false))
            {
                unit.animate();
            }
            unitInCombatLock.readLock().unlock();
        }

        @Override
        public void run() {
            while (isGameActive.get())
            {
                realSleepTime = SystemClock.uptimeMillis() + DATA_SLEEP_TIME;
                updateAnime();
                realSleepTime -= SystemClock.uptimeMillis();
                if (realSleepTime > 0)
                {
                    try {
                        Thread.sleep(realSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // thread to draw into canvas
    // and tell ui thread to update
    private class UpdateScreen implements Runnable
    {
        private long realSleepTime;
        private void updateScreen()
        {
            canvas.drawBitmap(level.getPortrait(), level.getX(), level.getY(), paint);
            canvas.drawBitmap(level.getTerrain().getPortrait(), level.getTerrain().getX(), level.getTerrain().getY(), paint);
            canvas.drawBitmap(castle.get(true).getPortrait(), castle.get(true).getX(), castle.get(true).getY(), paint);
            canvas.drawBitmap(castle.get(false).getPortrait(), castle.get(false).getX(), castle.get(false).getY(), paint);
            unitInCombatLock.readLock().lock();
            for (Unit unit : unitInCombat.get(true))
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.getX(), unit.getY(), paint);
            }
            for (Unit unit : unitInCombat.get(false))
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.getX(), unit.getY(), paint);
            }
            unitInCombatLock.readLock().unlock();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    gameScreen.setImageBitmap(screen);
                    gameScreen.bringToFront();
                    if (currentState.get() == StateId.PREPARE)
                        unitMenu.bringToFront();
                    else if (currentState.get() == StateId.COMBAT)
                    {
                        attackerInfo.get(TextId.NAME).setText(attacker.getName());
                        attackerInfo.get(TextId.HP).setText(attacker.getHp().toString() + "/" + attacker.getMaxHp().toString());
                        attackerInfo.get(TextId.ATTACK).setText(attacker.getAttack().toString());
                        attackerInfo.get(TextId.DEFENSE).setText(attacker.getDefense().toString());
                        attackerInfo.get(TextId.SPEED).setText(attacker.getSpeed().toString());
                        defenderInfo.get(TextId.NAME).setText(defender.getName().toString());
                        defenderInfo.get(TextId.HP).setText(defender.getHp().toString() + "/" + defender.getMaxHp().toString());
                        defenderInfo.get(TextId.ATTACK).setText(defender.getAttack().toString());
                        defenderInfo.get(TextId.DEFENSE).setText(defender.getDefense().toString());
                        defenderInfo.get(TextId.SPEED).setText(defender.getSpeed().toString());
                        combatBoard.bringToFront();
                    }

                    if (SystemData.isIfOutput()) System.out.println("after ui set before wake: " + SystemClock.uptimeMillis());

                    // tell screen thread ui is updated
                    synchronized (screenCondition)
                    {
                        screenCondition.finished = true;
                        screenCondition.notify();
                    }
                }
            });
        }

        @Override
        public void run() {
            while (isGameActive.get()) {
                realSleepTime = SystemClock.uptimeMillis() + screenSleepTime;
                updateScreen();
                if (SystemData.isIfOutput()) System.out.println("before wait after ui: " + SystemClock.uptimeMillis());
                // wait until ui thread finish its work
                try {
                    synchronized (screenCondition)
                    {
                        while (!screenCondition.finished) screenCondition.wait();
                        screenCondition.finished = false;
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
                        Thread.sleep(realSleepTime);
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
    private HashMap<ThreadId, Future<?>> waits;
    private HashMap<Boolean, Unit[]> unitInDeck;
    private HashMap<Boolean, Item[]> itemInDeck;
    private HashMap<Boolean, Unit[]> unitInStock;
    private HashMap<Boolean, Item[]> itemInStock;
    private HashMap<Boolean, ArrayList<Unit>> unitInCombat;
    private HashMap<Boolean, Unit> castle;
    private Unit attacker;
    private Unit defender;
    private Unit activeUnit;
    private Target target;
    private Level level;
    private Terrain terrain;

    // game control
    private UpdateData updateData;
    private UpdateScreen updateScreen;
    private UpdateAnime updateAnime;
    private Integer currentUnit;
    private final long MILISECOND = 1000;
    private final float DATA_PER_SECOND = SystemData.GAME_SPEED;
    private final long MAX_FPS = 60;
    private final long MIN_FPS = 5;
    private float framePerSecond;
    private long screenSleepTime;
    private final long DATA_SLEEP_TIME = MILISECOND / (long) DATA_PER_SECOND;
    private AtomicBoolean isGameActive;
    private AtomicState currentState;
    private ScreenCondition screenCondition;
    private AnimeCondition animeCondition;
    private ReentrantReadWriteLock unitInCombatLock;
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
    private HashMap<TextId, TextView> attackerInfo;
    private HashMap<TextId, TextView> defenderInfo;
    private Canvas canvas;
    private Paint paint;
    private Bitmap screen;

    private HorizontalScrollView screenView;

    private MultithreadGameLogic(Activity activity, Handler hander, Terrain terrain)
    {
        this.screenView = activity.findViewById(R.id.GameScreenWrapper);
        this.attacker = null;
        this.defender = null;
        this.unitInCombatLock = new ReentrantReadWriteLock();
        this.currentState.set(StateId.PREPARE);
        this.activity = activity;
        this.isPlayer1 = true;
        for (ThreadId id : ThreadId.values())
            this.waits.put(id, null);
        this.unitInCombat.put(true, new ArrayList<Unit>(25));
        this.unitInCombat.put(false, new ArrayList<Unit>(25));
        this.unitInDeck.put(true, new Unit[UNIT_CARD_NUM]);
        this.unitInDeck.put(false, new Unit[UNIT_CARD_NUM]);
        this.itemInDeck.put(true, new Item[ITEM_CARD_NUM]);
        this.itemInDeck.put(false, new Item[ITEM_CARD_NUM]);
        this.updateData = new UpdateData();
        this.updateScreen = new UpdateScreen();
        this.updateAnime = new UpdateAnime();
        this.framePerSecond = 30;
        this.paint = new Paint();
        this.isGameActive.set(false);
        this.handler = hander;
        this.random = new Random();
        this.screenCondition = new ScreenCondition();
        this.animeCondition = new AnimeCondition();
        this.terrain = terrain;
        this.gameScreen = activity.findViewById(R.id.GameScreen);
        this.unitMenu = activity.findViewById(R.id.UnitMenu);
        this.combatBoard = activity.findViewById(R.id.CombatBoard);
        this.attackerInfo.put(TextId.NAME, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(TextId.HP, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(TextId.ATTACK, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(TextId.DEFENSE, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(TextId.SPEED, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(TextId.NAME, (TextView) activity.findViewById(R.id.DefenderName));
        this.attackerInfo.put(TextId.HP, (TextView) activity.findViewById(R.id.DefenderHp));
        this.attackerInfo.put(TextId.ATTACK, (TextView) activity.findViewById(R.id.DefenderAttack));
        this.attackerInfo.put(TextId.DEFENSE, (TextView) activity.findViewById(R.id.DefenderDefense));
        this.attackerInfo.put(TextId.SPEED, (TextView) activity.findViewById(R.id.DefenderSpeed));
        this.rearBattleField.put(true, terrain.getBattleFields()[0]);
        this.rearBattleField.put(false, terrain.getBattleFields()[terrain.getBattleFieldNum() - 1]);
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(screen);
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
        this.castle.put(true, SystemData.createUnit(Id.Unit.HOLY_CASTLE.ordinal()));
        this.castle.put(false, SystemData.createUnit(Id.Unit.EVIL_CASTLE.ordinal()));
        this.castle.get(false).setX(backgroundWidth - castle.get(false).getPortrait().getHeight());
        for (int i = 0; i < castle.get(true).getPortrait().getWidth() / Unit.PIXEL; ++i)
            this.rearBattleField.get(true).getTiles()[i].setUnit(castle.get(true));
        for (int i = rearBattleField.get(false).getTiles().length - 1; i >= castle.get(false).getPortrait().getWidth() / Unit.PIXEL; ++i)
            this.rearBattleField.get(false).getTiles()[i].setUnit(castle.get(false));
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

    public void postCard(final ToggleButton toggleButton, final Integer cost, final int resource)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toggleButton.setBackgroundResource(resource);
                toggleButton.setTextOff(cost.toString());
                toggleButton.setTextOn(cost.toString());
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

    public void placeUnit()
    {

    }

    // importatn when switching players
    public void switchPlayer()
    {
        Terrain.Tile tile = rearBattleField.get(isPlayer1).findFirstAvailableTile();
        Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1)[i].getId()).setPlayer1();
        unit.setX(tile.getX());
        unitInCombatLock.writeLock().lock();
        unitInCombat.get(isPlayer1).add(unit);
        unitInCombatLock.writeLock().unlock();
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
                SystemData.oneTimeThread.execute(new Runnable() {
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
                                    Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1)[i].getId()).setPlayer1();
                                    unit.setX(tile.getX());
                                    unitInCombatLock.writeLock().lock();
                                    unitInCombat.get(isPlayer1).add(unit);
                                    unitInCombatLock.writeLock().unlock();
                                    generateUnitCard((ToggleButton) units.getChildAt(i), i);
                                }
                            for (int i = 0; i < items.getChildCount(); ++i)
                                if ( ((ToggleButton) items.getChildAt(i)).isChecked()) {
                                    itemInDeck.get(isPlayer1)[i].use();
                                    generateItemCard((ToggleButton) items.getChildAt(i), i);
                                }
                            currentState.set(StateId.MOVING);
                            synchronized (animeCondition)
                            {
                                animeCondition.hasUserInput = true;
                                animeCondition.notify();
                            }
                        }
                    }
                });
            }
        });
    }

    public void onResume()
    {
        waits.put(ThreadId.DATA, SystemData.gameThreads.submit(updateData));
        waits.put(ThreadId.SCREEN, SystemData.gameThreads.submit(updateScreen));
        waits.put(ThreadId.ANIME, SystemData.gameThreads.submit(updateAnime));
    }

    public void onPause()
    {
        isGameActive.set(false);
        synchronized (screenCondition)
        {
            screenCondition.finished = true;
            screenCondition.notifyAll();
        }
        for (ThreadId id : ThreadId.values())
            if (waits.get(id) != null)
                try {
                    waits.get(id).get();
                    waits.put(id, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
    }
}
