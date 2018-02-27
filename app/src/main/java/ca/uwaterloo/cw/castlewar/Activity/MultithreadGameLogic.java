package ca.uwaterloo.cw.castlewar.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public class AtomicState
    {
        private Id.GameState state;
        synchronized Id.GameState get()
        {
            return state;
        }
        synchronized void set(Id.GameState state)
        {
            this.state = state;
        }
    }

    // simple condition lock
    public class ScreenCondition extends Object
    {
        public boolean finished = false;
    }

    public class DataCondition extends Object
    {
        public boolean hasUserInput = false;
    }

    // combat environment
    private class Combat {
        private final Boolean ATTACKER = true;
        private final Boolean DEFENDER = false;
        private HashMap<Boolean, Unit> units = new HashMap<>(2);
        private final int MAX_TURN = 5;
        private Boolean[] turns;

        public Combat(Unit attacker, Unit defender) {
            this.units.put(ATTACKER,attacker);
            this.units.put(DEFENDER,defender);
            this.turns = new Boolean[MAX_TURN];

            // initialize turns
            for (int i = 0; i < MAX_TURN; ++i) {
                turns[i] = i % 2 == 0 ? ATTACKER : DEFENDER;
            }

            if (attacker.speed.get() > defender.speed.get()) {
                turns[MAX_TURN - 1] = ATTACKER;
            } else if (attacker.speed.get() > defender.speed.get()) {
                turns[MAX_TURN - 1] = DEFENDER;
            } else {
                turns[MAX_TURN] = null;
            }
        }

        public void start(){
            // execute turns
            for (Boolean isAttacker : turns){
                if (isAttacker != null){
                    // prepare attack
                    int damage = units.get(isAttacker).attack.get() - units.get(!isAttacker).defense.get();
                    if (damage < 0) damage = 0;

                    // attack

                    // take damage
                    int hpAfterDamage = units.get(!isAttacker).takeDamage(units.get(isAttacker), damage);
                    animateSet(units.get(!isAttacker).hp.get(), hpAfterDamage, units.get(!isAttacker).hp);

                    if (units.get(true).isDead() || units.get(false).isDead()) return;
                }
            }
        }
    }

    // thread to update logic
    private class UpdateData implements Runnable
    {
        Combat combat;
        private void moveAnime(Unit unit){
            // move states
            animateMove(unit.x.get(), unit.getMoveTile().getX(), unit.x, unit);
            unit.getCurrentTile().setUnit(null);
            unit.setCurrentTile(unit.getMoveTile());
            unit.getMoveTile().setUnit(unit);
        }

        private void combatAnime(){
            // target the defender
            target.x.set(attacker.x.get());
            target.setVisible(true);
            animateMove(target.x.get(), defender.x.get(), target.x, target);

            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    combatBoard.setVisibility(View.VISIBLE);
                }
            });

            // hide tart
            target.setVisible(false);

            // set up combat
            currentState.set(Id.GameState.COMBAT);
            combat = new Combat(attacker, defender);
            combat.start();

            if (attacker.isDead()){
                attacker.getCurrentTile().setUnit(null);
                unitInCombatLock.writeLock().lock();
                unitInCombat.get(attacker.isPlayer1()).remove(attacker);
                unitInCombatLock.writeLock().unlock();
            }
            if (defender.isDead()){
                defender.getCurrentTile().setUnit(null);
                unitInCombatLock.writeLock().lock();
                unitInCombat.get(defender.isPlayer1()).remove(defender);
                unitInCombatLock.writeLock().unlock();
            }

            currentState.set(Id.GameState.MOVING);
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    combatBoard.setVisibility(View.INVISIBLE);
                }
            });
        }

        private void updateData()
        {
            // set unitView
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    unitMenu.setVisibility(View.VISIBLE);
                }
            });

            // bring back screen
            screenFocusOn(castle.get(isPlayer1));
            currentState.set(Id.GameState.PREPARE);

            // wait for user input

            synchronized (dataCondition)
            {
                while(!dataCondition.hasUserInput)
                    try {
                        dataCondition.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                dataCondition.hasUserInput = false;
            }

            if (!isGameActive.get()) return;

            // move units by its adding ordering
            unitInCombatLock.readLock().lock();
            for (Unit unit : unitInCombat.get(isPlayer1))
            {
                unit.decideStrategy(terrain);
                if (unit.getActionTile() != null) System.out.println("actioin tile is " + unit.getActionTile().getX());
                if (unit.getMoveTile() != null) System.out.println("move tile is " + unit.getMoveTile().getX());
                if (unit.getMoveTile() != null){
                    unit.changeDirection(unit.getMoveTile());
                    moveAnime(unit);
                }
                if (unit.getActionTile() != null){
                    // set attacker and defender
                    attacker = unit;
                    defender = unit.getActionTile().getUnit();
                    attacker.changeDirection(defender.getCurrentTile());
                    defender.changeDirection(attacker.getCurrentTile());
                    attacker.setAttacker(true);
                    defender.setAttacker(false);
                    combatAnime();
                }
            }
            unitInCombatLock.readLock().unlock();

            // if Ai
            if (isAi)
            {
                isPlayer1 = false;
                // bring screen
                screenFocusOn(castle.get(isPlayer1));

                // create units
                if (level.isInterval()){
                        while (rearBattleField.get(isPlayer1).getAvailableTileNum() > 0){
                            placeUnit(random.nextInt(level.getEnemies().length));
                        }
                } else{
                    for(int i = 0; i < level.getMaxUnit(); ++i){
                        if (rearBattleField.get(isPlayer1).getAvailableTileNum() > 0){
                            placeUnit(random.nextInt(level.getEnemies().length));
                        } else {
                            break;
                        }
                    }
                }
                // let units go
                // move units by its adding ordering
                unitInCombatLock.readLock().lock();
                for (Unit unit : unitInCombat.get(isPlayer1))
                {
                    unit.decideStrategy(terrain);
                    if (unit.getActionTile() != null) System.out.println("actioin tile is " + unit.getActionTile().getX());
                    if (unit.getMoveTile() != null) System.out.println("move tile is " + unit.getMoveTile().getX());
                    if (unit.getMoveTile() != null){
                        unit.changeDirection(unit.getMoveTile());
                        moveAnime(unit);
                    }
                    if (unit.getActionTile() != null){
                        // set attacker and defender
                        attacker = unit;
                        defender = unit.getActionTile().getUnit();
                        attacker.changeDirection(defender.getCurrentTile());
                        defender.changeDirection(attacker.getCurrentTile());
                        attacker.setAttacker(true);
                        defender.setAttacker(false);
                        combatAnime();
                    }
                }
                unitInCombatLock.readLock().unlock();

                isPlayer1 = true;
            } else{
                // switch player
                switchPlayer();
            }

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
                realSleepTime = SystemClock.uptimeMillis() + ANIME_SLEEP_TIME;
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
            screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(screen);
            canvas.drawBitmap(level.getPortrait(), level.x.get(), level.getY(), paint);
            canvas.drawBitmap(level.getTerrain().getPortrait(), level.getTerrain().x.get(), level.getTerrain().getY(), paint);
            canvas.drawBitmap(castle.get(true).getPortrait(), castle.get(true).x.get(), castle.get(true).getY(), paint);
            canvas.drawBitmap(castle.get(false).getPortrait(), castle.get(false).x.get(), castle.get(false).getY(), paint);
            for (Terrain.BattleField battleField : terrain.getBattleFields()){
                if (battleField.getId() == 0) continue;
                int x = battleField.getTiles()[0].getX();
                canvas.drawLine(x,0,x,SystemData.getScreenHeight(),paint);
            }
            unitInCombatLock.readLock().lock();
            if (target.isVisible()){
                canvas.drawBitmap(target.getPortrait(), target.x.get(), target.getY(), paint);
            }
            for (Unit unit : unitInCombat.get(true))
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.x.get(), unit.getY(), paint);
            }
            for (Unit unit : unitInCombat.get(false))
            {
                canvas.drawBitmap(unit.getMovingImage(), unit.x.get(), unit.getY(), paint);
            }
            unitInCombatLock.readLock().unlock();
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    gameScreen.setImageBitmap(screen);
                    gameScreen.bringToFront();
                    if (currentState.get() == Id.GameState.PREPARE){
                        unitMenu.bringToFront();
                    }
                    else if (currentState.get() == Id.GameState.COMBAT)
                    {
                        attackerInfo.get(Id.CombatBoard.NAME).setText(attacker.getName());
                        attackerInfo.get(Id.CombatBoard.HP).setText(attacker.hp.get() + "/" + attacker.maxHp.get());
                        attackerInfo.get(Id.CombatBoard.ATTACK).setText(attacker.attack.get());
                        attackerInfo.get(Id.CombatBoard.DEFENSE).setText(attacker.defense.get());
                        attackerInfo.get(Id.CombatBoard.SPEED).setText(attacker.speed.get());
                        defenderInfo.get(Id.CombatBoard.NAME).setText(defender.getName().toString());
                        defenderInfo.get(Id.CombatBoard.HP).setText(defender.hp.get() + "/" + defender.maxHp.get());
                        defenderInfo.get(Id.CombatBoard.ATTACK).setText(defender.attack.get());
                        defenderInfo.get(Id.CombatBoard.DEFENSE).setText(defender.defense.get());
                        defenderInfo.get(Id.CombatBoard.SPEED).setText(defender.speed.get());
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
                if (!isGameActive.get()) return;
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
    private final int CARD_NUM = 5;
    private final int DRAW_NUM = 2;
    private final int DRAW_COST = 3;
    private Activity activity;
    private HashMap<Id.Thread, Future<?>> waits = new HashMap<>(Id.Thread.values().length);
    private HashMap<Boolean, Unit[]> unitInDeck = new HashMap<>(2);
    private HashMap<Boolean, Item[]> itemInDeck = new HashMap<>(2);
    private HashMap<Boolean, Unit[]> unitInStock = new HashMap<>(2);
    private HashMap<Boolean, Item[]> itemInStock = new HashMap<>(2);
    private HashMap<Boolean, ArrayList<Unit>> unitInCombat = new HashMap<>(2);
    private HashMap<Boolean, Unit> castle = new HashMap<>(2);
    private Unit attacker;
    private Unit defender;
    private Target target = new Target();
    private Level level;
    private Terrain terrain;

    // game control
    private UpdateData updateData = new UpdateData();
    private UpdateScreen updateScreen = new UpdateScreen();
    private UpdateAnime updateAnime = new UpdateAnime();
    private final int BASIC_RECOVERY = 3;
    private final long MILISECOND = 1000;
    private final float ANIME_PER_SECOND = 10;
    private final long MAX_FPS = 60;
    private final long MIN_FPS = 5;
    private float framePerSecond;
    private long screenSleepTime;
    private final long ANIME_SLEEP_TIME = MILISECOND / (long) ANIME_PER_SECOND;
    private AtomicBoolean isGameActive = new AtomicBoolean();
    private AtomicState currentState = new AtomicState();
    private ScreenCondition screenCondition = new ScreenCondition();
    private DataCondition dataCondition = new DataCondition();
    private ReentrantReadWriteLock unitInCombatLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock buttonLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock prepareDoneLock = new ReentrantReadWriteLock();
    private Random random = new Random();
    private boolean isAi;
    private boolean isPlayer1;
    private HashMap<Boolean, Integer> cost = new HashMap<>(2);
    private HashMap<Boolean, Integer> maxCost = new HashMap<>(2);
    private HashMap<Boolean, Integer> costPerTurn = new HashMap<>(2);
    private HashMap<Boolean, Terrain.BattleField> rearBattleField = new HashMap<>(2);

    // screen control
    private final float SCROLL_PIXEL_PER_SECOND = 1000;
    private final float PIXEL_PER_SECOND = 500;
    private final float VALUE_PER_SECOND = 20;
    private final float UPDATE_PER_SECOND = 30;
    private final float SLEEP_TIME = 1000 / UPDATE_PER_SECOND;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final ImageView gameScreen;
    private final LinearLayout unitMenu;
    private final LinearLayout combatBoard;
    private HashMap<Id.CombatBoard, TextView> attackerInfo = new HashMap<>(Id.CombatBoard.values().length);
    private HashMap<Id.CombatBoard, TextView> defenderInfo = new HashMap<>(Id.CombatBoard.values().length);
    private final ImageButton[] unitImageButtons = new ImageButton[CARD_NUM];
    private final ImageButton[] itemImageButtons = new ImageButton[CARD_NUM];
    private final TextView[] unitCostTexts = new TextView[CARD_NUM];
    private final TextView[] itemCostTexts = new TextView[CARD_NUM];
    private final TextView costText;
    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap screen;
    private HorizontalScrollView screenView;

    private MultithreadGameLogic(Activity activity, Terrain terrain)
    {
        paint.setARGB(255, 0, 0,0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[] {30,10}, 0));
        this.screenView = activity.findViewById(R.id.GameScreenWrapper);
        this.attacker = null;
        this.defender = null;
        this.currentState.set(Id.GameState.PREPARE);
        this.activity = activity;
        for (Id.Thread id : Id.Thread.values())
            this.waits.put(id, null);
        this.unitInCombat.put(true, new ArrayList<Unit>(25));
        this.unitInCombat.put(false, new ArrayList<Unit>(25));
        this.framePerSecond = 30;
        this.isGameActive.set(false);
        this.terrain = terrain;
        this.gameScreen = activity.findViewById(R.id.GameScreen);
        this.unitMenu = activity.findViewById(R.id.UnitMenu);
        this.combatBoard = activity.findViewById(R.id.CombatBoard);
        LinearLayout units = activity.findViewById(R.id.Units);
        LinearLayout items = activity.findViewById(R.id.Items);
        LinearLayout unitCost = activity.findViewById(R.id.UnitCost);
        LinearLayout itemCost = activity.findViewById(R.id.ItemCost);
        for (int i = 0; i < CARD_NUM; ++i){
            unitImageButtons[i] = (ImageButton) units.getChildAt(i);
            itemImageButtons[i] = (ImageButton) items.getChildAt(i);
            unitCostTexts[i] = (TextView) unitCost.getChildAt(i);
            itemCostTexts[i] = (TextView) itemCost.getChildAt(i);
        }
        this.costText = activity.findViewById(R.id.cost);
        this.attackerInfo.put(Id.CombatBoard.NAME, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Id.CombatBoard.HP, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Id.CombatBoard.ATTACK, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Id.CombatBoard.DEFENSE, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Id.CombatBoard.SPEED, (TextView) activity.findViewById(R.id.AttackerName));
        this.defenderInfo.put(Id.CombatBoard.NAME, (TextView) activity.findViewById(R.id.DefenderName));
        this.defenderInfo.put(Id.CombatBoard.HP, (TextView) activity.findViewById(R.id.DefenderHp));
        this.defenderInfo.put(Id.CombatBoard.ATTACK, (TextView) activity.findViewById(R.id.DefenderAttack));
        this.defenderInfo.put(Id.CombatBoard.DEFENSE, (TextView) activity.findViewById(R.id.DefenderDefense));
        this.defenderInfo.put(Id.CombatBoard.SPEED, (TextView) activity.findViewById(R.id.DefenderSpeed));
        this.rearBattleField.put(true, terrain.getBattleFields()[0]);
        this.rearBattleField.put(false, terrain.getBattleFields()[terrain.getBattleFieldNum() - 1]);
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
        this.castle.put(true, SystemData.createUnit(Id.Unit.HOLY_CASTLE.ordinal()));
        this.castle.put(false, SystemData.createUnit(Id.Unit.EVIL_CASTLE.ordinal()));
        this.castle.get(true).setPlayer1(true);
        this.castle.get(false).setPlayer1(false);
        this.castle.get(false).x.set(backgroundWidth - castle.get(false).getPortrait().getWidth());
        for (int i = 0; i < castle.get(true).getPortrait().getWidth() / SystemData.PIXEL; ++i){
            this.rearBattleField.get(true).getTiles()[i].setUnit(castle.get(true));
        }
        for (int i = rearBattleField.get(false).getTiles().length - 1; i >= castle.get(false).getPortrait().getWidth() / SystemData.PIXEL; --i){
            this.rearBattleField.get(false).getTiles()[i].setUnit(castle.get(false));
        }
        this.costPerTurn.put(true, BASIC_RECOVERY);
        this.costPerTurn.put(false, BASIC_RECOVERY);
    }

    public MultithreadGameLogic(Activity activity, Level level, Unit[] unitInStockPlayer1, Item[] itemInStockPlayer1) {
        this(activity, level.getTerrain());
        this.isAi = true;
        this.level = level;
        this.unitInDeck.put(true, new Unit[CARD_NUM]);
        this.unitInDeck.put(false, new Unit[level.getEnemies().length]);
        this.itemInDeck.put(true, new Item[CARD_NUM]);
        this.itemInDeck.put(false, new Item[level.getItems().length]);
        this.unitInStock.put(true, unitInStockPlayer1);
        this.unitInStock.put(false, level.getEnemies());
        this.itemInStock.put(true, itemInStockPlayer1);
        this.itemInStock.put(false, itemInStockPlayer1);
        this.cost.put(true, UserProfile.getMaxCost());
        this.maxCost.put(true, UserProfile.getMaxCost());
        this.isPlayer1 = true;
        for (int i = 0; i < CARD_NUM; ++i){
            generateItemCard(i);
            generateUnitCard(i);
        }
        this.unitInDeck.put(false, level.getEnemies());
        this.itemInDeck.put(false, level.getItems());
        this.isPlayer1 = false;
        switchPlayer();
        initializeButtons();
    }

    public MultithreadGameLogic(Activity activity, Terrain terrain, Unit[] unitInStockPlayer1, Unit[] unitInStockPlayer2) {
        this(activity, terrain);
        this.isAi = true;
        this.terrain = terrain;
        this.unitInStock.put(true, unitInStockPlayer1);
        this.unitInStock.put(false, unitInStockPlayer2);
        /*
        this.itemInStock.put(true, itemInStockPlayer1);
        this.itemInDeck.put(false, level.getItems());
        */
        this.cost.put(true, UserProfile.getMaxCost());
        this.maxCost.put(true, UserProfile.getMaxCost());
        for (int i = 0; i < CARD_NUM; ++i){
            isPlayer1 = true;
            generateItemCard(i);
            generateUnitCard(i);
            isPlayer1 = false;
            generateUnitCard(i);
            generateItemCard(i);
        }
        initializeButtons();

        // remember to initialize unit in deck for player 2
    }

    public void draw(){
        int countUnit = 0, countItem = 0;
        for (int i = 0; i < CARD_NUM; ++i){
            if (unitInDeck.get(isPlayer1)[i] == null){
                if (countUnit < DRAW_NUM){
                    generateUnitCard(i);
                    postUnitCard(i);
                    countUnit++;
                }
            }
            if (itemInDeck.get(isPlayer1)[i] == null){
                if (countItem < DRAW_NUM){
                    generateItemCard(i);
                    postItemCard(i);
                    countItem++;
                }
            }
        }
    }


    public void generateUnitCard(int position)
    {
        final Unit unit = SystemData.createUnit(unitInStock.get(isPlayer1)[random.nextInt(unitInStock.get(isPlayer1).length)].getId());
        unitInDeck.get(isPlayer1)[position] = unit;
    }

    public void generateItemCard(int position)
    {
        final Item item = SystemData.createItem(itemInStock.get(isPlayer1)[random.nextInt(itemInStock.get(isPlayer1).length)].getId());
        itemInDeck.get(isPlayer1)[position] = item;
    }

    public void postUnitCard(final int position) {
        final Unit unit = unitInDeck.get(isPlayer1)[position];
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                unitImageButtons[position].setImageBitmap(unit != null ? unit.getPortrait() : SystemData.getEmptyIcon());
                unitCostTexts[position].setText(unit != null ? unit.cost.toString() : "0");
            }
        });
    }
    public void postItemCard(final int position) {
        final Item item = itemInDeck.get(isPlayer1)[position];
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                itemImageButtons[position].setImageBitmap(item != null ? item.getPortrait() : SystemData.getEmptyIcon());
                itemCostTexts[position].setText(item != null ? item.cost.toString() : "0");
            }
        });
    }

    public void postCost(){
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                costText.setText(cost.get(isPlayer1).toString() + "/" + maxCost.get(isPlayer1).toString());
            }
        });
    }

    public void setCostBy(int delta){
        int currentCost = cost.get(isPlayer1) + delta;
        if (currentCost > maxCost.get(isPlayer1)) currentCost = maxCost.get(isPlayer1);
        if (currentCost < 0) currentCost = 0;
        cost.put(isPlayer1, currentCost);
    }

    public void placeUnit(final int position)
    {
        SystemData.oneTimeThread.execute(new Runnable() {
            @Override
            public void run() {
                buttonLock.writeLock().lock();
                if (unitInDeck.get(isPlayer1)[position] == null || currentState.get() != Id.GameState.PREPARE) return;

                Terrain.Tile tile = rearBattleField.get(isPlayer1).findFirstAvailableTile(isPlayer1);
                Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1)[position].getId());
                unit.x.set(rearBattleField.get(isPlayer1).getTiles()[isPlayer1 ? 2 : 7].getX());
                unit.setPlayer1(isPlayer1);
                unit.setLeft(!isPlayer1);
                unit.setCurrentTile(tile);
                tile.setUnit(unit);
                if (!isAi || isPlayer1) {
                    unitInDeck.get(isPlayer1)[position] = null;
                    postUnitCard(position);
                    setCostBy(-unit.cost.get());
                    postCost();
                }
                unitInCombatLock.writeLock().lock();
                unitInCombat.get(isPlayer1).add(unit);
                unitInCombatLock.writeLock().unlock();
                prepareDoneLock.readLock().lock();
                buttonLock.writeLock().unlock();
                animateMove(unit.x.get(), tile.getX(), unit.x, unit);
                buttonLock.readLock().unlock();
            }
        });
    }

    public void useItem(final int position){
        SystemData.oneTimeThread.execute(new Runnable() {
            @Override
            public void run() {
                buttonLock.writeLock().lock();
                Item item = itemInDeck.get(isPlayer1)[position];
                if (!isAi || isPlayer1) {
                    itemInDeck.get(isPlayer1)[position] = null;
                    postItemCard(position);
                    setCostBy(-item.cost.get());
                    postCost();
                }
                buttonLock.writeLock().unlock();
            }
        });
    }

    // importatn when switching players
    public void switchPlayer()
    {
        isPlayer1 = !isPlayer1;
        for (int i = 0; i < CARD_NUM; ++i){
            postUnitCard(i);
            postItemCard(i);
        }
        postCost();
    }

    public void initializeButtons() {
        Button endTurn = activity.findViewById(R.id.EndTurn);
        Button redraw = activity.findViewById(R.id.Redraw);
        final Button draw = activity.findViewById(R.id.Draw);

        for (int i = 0; i < CARD_NUM; ++i) {
            final int position = i;
            unitImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cost.get(isPlayer1) >= unit.cost.get()) {
                        placeUnit(position);

                    }
                }
            });


            itemImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cost.get(isPlayer1) >= item.cost.get()) {
                        useItem(position);
                        postItemCard(position);
                        setCostBy(-item.cost.get());
                        postCost();
                    }
                }
            });
        }

        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemData.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        currentState.set(Id.GameState.MOVING);

                        // set prepare visible
                        SystemData.postToUi(new Runnable() {
                            @Override
                            public void run() {
                                unitMenu.setVisibility(View.INVISIBLE);
                            }
                        });
                        synchronized (dataCondition) {
                            dataCondition.hasUserInput = true;
                            dataCondition.notify();
                        }
                        // recover cost
                        setCostBy(costPerTurn.get(isPlayer1));
                    }
                });
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemData.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (cost.get(isPlayer1) >= DRAW_COST){
                            draw();
                            setCostBy(-DRAW_COST);
                            postCost();
                        }
                    }
                });
            }
        });
    }


    public void screenFocusOn(GameObject gameObject) {
        int x = gameObject.x.get() - SystemData.getScreenWidth() / 2 + gameObject.getPortrait().getWidth() / 2;
        if (x < 0) x = 0;
        else if (x > backgroundWidth - SystemData.getScreenWidth()) x = backgroundWidth - SystemData.getScreenWidth();
        final int copy = x;
        final float scrollTime = Math.abs(screenView.getScrollX() - copy) * MILISECOND / SCROLL_PIXEL_PER_SECOND;
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anime = ObjectAnimator.ofInt(screenView, "scrollX",copy);
                anime.setDuration((int) scrollTime);
                anime.start();
            }
        });

        try {
            Thread.sleep((int) scrollTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    public void animatePost(int start, int end, final TextView textView) {
        boolean isPositive = end >= start ? true : false;
        float delta = (float) Math.abs((end - start));
        float updateTimes = delta / VALUE_PER_SECOND  * UPDATE_PER_SECOND;
        float cache = start;
        float increment = delta / updateTimes;

        while (isPositive ? cache < end : cache > end) {
            final int copy = (int) cache;
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    textView.setText(copy);
                }
            });

            cache = isPositive ? cache + increment : cache - increment;

            if (isPositive ? cache > end : cache < end) {
                cache = end;
            }
            try {
                Thread.sleep((long) SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    */

    public void animateMove(int start, int end, AtomicInteger value, GameObject gameObject) {
        boolean isPositive = end >= start ? true : false;
        float delta = (float) Math.abs((end - start));
        float updateTimes = delta / PIXEL_PER_SECOND * UPDATE_PER_SECOND;
        float cache = start;
        float increment = delta / updateTimes;

        screenFocusOn(gameObject);

        while (isPositive ? cache < end : cache > end) {
            final int copy = (int) cache;
            if (copy > screenView.getScrollX() + SystemData.getScreenWidth() * 0.9 ||
                    copy < screenView.getScrollX() + SystemData.getScreenWidth() * 0.1) screenFocusOn(gameObject);
            value.set(copy);

            cache = isPositive ? cache + increment : cache - increment;

            if (isPositive ? cache > end : cache < end){
                cache = end;
            }
            try {
                Thread.sleep((long) SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // last time no need to sleep, no increment, set to end directly
        if (end > screenView.getScrollX() + SystemData.getScreenWidth() * 0.8 ||
                end < screenView.getScrollX() + SystemData.getScreenWidth() * 0.2) screenFocusOn(gameObject);
        value.set(end);
    }

    public void animateSet(int start, int end, AtomicInteger value) {
        boolean isPositive = end >= start ? true : false;
        float delta = (float) Math.abs((end - start));
        float updateTimes = ((float) Math.abs((end - start)) / VALUE_PER_SECOND) * UPDATE_PER_SECOND;
        float cache = start;
        float increment = delta / updateTimes;

        while (isPositive ? cache < end : cache > end) {
            final int copy = (int) cache;
            value.set(copy);

            cache = isPositive ? cache + increment : cache - increment;

            if (isPositive ? cache > end : cache < start){
                cache = end;
            }
            try {
                Thread.sleep((long) SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // last time no need to sleep, no increment, set to end directly
        value.set(end);
    }


    public void onResume()
    {
        this.isGameActive.set(true);
        waits.put(Id.Thread.DATA, SystemData.gameThreads.submit(updateData));
        waits.put(Id.Thread.SCREEN, SystemData.gameThreads.submit(updateScreen));
        waits.put(Id.Thread.ANIME, SystemData.gameThreads.submit(updateAnime));
    }

    public void onPause()
    {
        isGameActive.set(false);
        synchronized (screenCondition)
        {
            screenCondition.finished = true;
            screenCondition.notifyAll();
        }
        synchronized (dataCondition)
        {
            dataCondition.hasUserInput = true;
            dataCondition.notifyAll();
        }
        for (Id.Thread id : Id.Thread.values())
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
