package ca.uwaterloo.cw.castlewar.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import ca.uwaterloo.cw.castlewar.Model.Castle;
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
                turns[MAX_TURN - 1] = null;
            }
        }

        public void start(){
            // execute turns
            for (Boolean isAttacker : turns){
                if (isAttacker != null){
                    // prepare attack
                    int damage = units.get(isAttacker).attack.get() - units.get(!isAttacker).defense.get();
                    if (damage < 0) damage = 0;


                    // take damage
                    int hpAfterDamage = units.get(!isAttacker).takeDamage(units.get(isAttacker), damage);
                    animatePost(units.get(!isAttacker).hp.get(), hpAfterDamage, units.get(!isAttacker).hp, !isAttacker ? attackerInfo.get(Id.CombatBoard.HP) : defenderInfo.get(Id.CombatBoard.HP));

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
            currentState.set(Id.GameState.MOVING);
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

            // setup board
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    attackerInfo.get(Id.CombatBoard.NAME).setText(attacker.getName());
                    attackerInfo.get(Id.CombatBoard.HP).setText(attacker.hp.get() + "/" + attacker.maxHp.get());
                    attackerInfo.get(Id.CombatBoard.ATTACK).setText(attacker.attack.toString());
                    attackerInfo.get(Id.CombatBoard.DEFENSE).setText(attacker.defense.toString());
                    attackerInfo.get(Id.CombatBoard.SPEED).setText(attacker.speed.toString());
                    defenderInfo.get(Id.CombatBoard.NAME).setText(defender.getName());
                    defenderInfo.get(Id.CombatBoard.HP).setText(defender.hp.get() + "/" + defender.maxHp.get());
                    defenderInfo.get(Id.CombatBoard.ATTACK).setText(defender.attack.toString());
                    defenderInfo.get(Id.CombatBoard.DEFENSE).setText(defender.defense.toString());
                    defenderInfo.get(Id.CombatBoard.SPEED).setText(defender.speed.toString());
                }
            });

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
            // bring back screen
            screenFocusOn(castle.get(isPlayer1));
            currentState.set(Id.GameState.PREPARE);
            // set unitView
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    unitMenu.setVisibility(View.VISIBLE);
                }
            });

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

            // find first ready unit
            while (true){
                Unit unit = null;
                unitInCombatLock.readLock().lock();
                for (Unit iterator : unitInCombat.get(isPlayer1)){
                    if (iterator.isReady()){
                        unit = iterator;
                        break;
                    }
                }
                unitInCombatLock.readLock().unlock();
                // check if all units have done
                if (unit == null) break;
                // decide action and move
                unit.decideStrategy(terrain);
                // move
                if (unit.getMoveTile() != null){
                    unit.changeDirection(unit.getMoveTile());
                    moveAnime(unit);
                }
                // go to combat
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
                // set done
                unit.setReady(false);

                // check death
                unitInCombatLock.writeLock().lock();
                for (Unit looper : unitInCombat.get(true)){
                    if (looper.isDead()) unitInCombat.get(true).remove(looper);
                }
                for (Unit looper : unitInCombat.get(false)){
                    if (looper.isDead()) unitInCombat.get(false).remove(looper);
                }
                unitInCombatLock.writeLock().unlock();

                // check win
                if (castle.get(true).isDead()){
                    // add something in the future
                    activity.onBackPressed();
                } else if (castle.get(false).isDead()){
                    // add something
                    activity.onBackPressed();
                }
            }

            // if Ai
            if (isAi)
            {
                isPlayer1 = false;
                // bring screen
                screenFocusOn(castle.get(isPlayer1));

                currentState.set(Id.GameState.PREPARE);

                ArrayList<Future<?>> waitForAnime = new ArrayList<>(5);

                // create units
                if (level.isInterval()){
                        while (rearBattleField.get(isPlayer1).getAvailableTileNum() > 0){
                            waitForAnime.add(placeUnit(random.nextInt(level.getEnemies().length)));
                        }
                } else{
                    for(int i = 0; i < level.getMaxUnit(); ++i){
                        if (rearBattleField.get(isPlayer1).getAvailableTileNum() > 0){
                            waitForAnime.add(placeUnit(random.nextInt(level.getEnemies().length)));
                        } else {
                            break;
                        }
                    }
                }
                for (Future<?> wait : waitForAnime){
                    try {
                        wait.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                currentState.set(Id.GameState.MOVING);
                // let units go
                // move units by its adding ordering
                while (true){
                    Unit unit = null;
                    unitInCombatLock.readLock().lock();
                    for (Unit iterator : unitInCombat.get(isPlayer1)){
                        if (iterator.isReady()){
                            unit = iterator;
                            break;
                        }
                    }
                    unitInCombatLock.readLock().unlock();
                    // check if all units have done
                    if (unit == null) break;
                    // decide action and move
                    unit.decideStrategy(terrain);
                    // move
                    if (unit.getMoveTile() != null){
                        unit.changeDirection(unit.getMoveTile());
                        moveAnime(unit);
                    }
                    // go to combat
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
                    // set done
                    unit.setReady(false);

                    // check death
                    unitInCombatLock.writeLock().lock();
                    for (Unit looper : unitInCombat.get(true)){
                        if (looper.isDead()) unitInCombat.get(true).remove(looper);
                    }
                    for (Unit looper : unitInCombat.get(false)){
                        if (looper.isDead()) unitInCombat.get(false).remove(looper);
                    }
                    unitInCombatLock.writeLock().unlock();

                    // check win
                    if (castle.get(true).isDead()){
                        // add something in the future
                        activity.onBackPressed();
                    } else if (castle.get(false).isDead()){
                        // add something
                        activity.onBackPressed();
                    }
                }
                isPlayer1 = true;
            } else{
                // switch player
                switchPlayer();
            }

            // reset isReady for units
            unitInCombatLock.writeLock().lock();
            for (Unit unit : unitInCombat.get(isPlayer1)){
                unit.setReady(true);
            }
            unitInCombatLock.writeLock().unlock();
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
                updateAnime();
                try {
                    Thread.sleep(CONSTANT_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
    private final long MAX_FPS = 60;
    private final long MIN_FPS = 5;
    private float framePerSecond;
    private long screenSleepTime;
    private final long MILISECOND = 1000;
    private final float SCROLL_PIXEL_PER_SECOND = 1000; // speed of screen
    private final float PIXEL_PER_SECOND = 500; // speed of character
    private final float VALUE_PER_SECOND = 20;  // speed of text
    private final float ANIME_PER_SECOND = 10;  // this is the speed of sending posts to ui thread from other threads than canvas thread
    private final float LOGIC_PER_SECOND = 30;  // this is the speed of updates of normal background thread
    private final float CONSTANT_PER_SECOND = 15; // this is the speed of updates constant anime, music and so on
    private final long ANIME_SLEEP_TIME = MILISECOND / (long) ANIME_PER_SECOND;
    private final long LOGIC_SLEEP_TIME = MILISECOND / (long) LOGIC_PER_SECOND;
    private final long CONSTANT_SLEEP_TIME = MILISECOND / (long) CONSTANT_PER_SECOND;
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
    private ProgressBar progressBar;

    private MultithreadGameLogic(Activity activity, Terrain terrain)
    {
        this.progressBar = activity.findViewById(R.id.GameLoading);
        this.paint.setARGB(255, 0, 0,0);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(10);
        this.paint.setPathEffect(new DashPathEffect(new float[] {30,10}, 0));
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
        this.attackerInfo.put(Id.CombatBoard.HP, (TextView) activity.findViewById(R.id.AttackerHp));
        this.attackerInfo.put(Id.CombatBoard.ATTACK, (TextView) activity.findViewById(R.id.AttackerAttack));
        this.attackerInfo.put(Id.CombatBoard.DEFENSE, (TextView) activity.findViewById(R.id.AttackerDefense));
        this.attackerInfo.put(Id.CombatBoard.SPEED, (TextView) activity.findViewById(R.id.AttackerSpeed));
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
        int leftCastlePosition = castle.get(true).getPortrait().getWidth() / SystemData.PIXEL / 2;
        int rightCastlePosition = castle.get(false).getPortrait().getWidth() / SystemData.PIXEL * 3 / 2;
        for (int i = 0; i < castle.get(true).getPortrait().getWidth() / SystemData.PIXEL; ++i){
            this.rearBattleField.get(true).getTiles()[i].setUnit(castle.get(true));
            if (i == leftCastlePosition) this.castle.get(true).setCurrentTile(this.rearBattleField.get(true).getTiles()[i]);
        }
        for (int i = rearBattleField.get(false).getTiles().length - 1; i >= castle.get(false).getPortrait().getWidth() / SystemData.PIXEL; --i){
            this.rearBattleField.get(false).getTiles()[i].setUnit(castle.get(false));
            if (i == rightCastlePosition) this.castle.get(false).setCurrentTile(this.rearBattleField.get(false).getTiles()[i]);
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

    public Future<?> placeUnit(final int position)
    {
        return SystemData.oneTimeThread.submit(new Runnable() {
            @Override
            public void run() {
                buttonLock.writeLock().lock();
                if (unitInDeck.get(isPlayer1)[position] == null
                        || currentState.get() != Id.GameState.PREPARE) {
                    buttonLock.writeLock().unlock();
                    return;
                }
                if (isPlayer1 || !isAi){
                    if (unitInDeck.get(isPlayer1)[position].cost.get() > cost.get(isPlayer1)) {
                        buttonLock.writeLock().unlock();
                        return;
                    }
                }
                Terrain.Tile tile = rearBattleField.get(isPlayer1).findFirstAvailableTile(isPlayer1);
                Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1)[position].getId());
                unit.x.set(castle.get(isPlayer1).getCurrentTile().getX());
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
                prepareDoneLock.readLock().unlock();
            }
        });
    }

    public Future<?> useItem(final int position){
        return SystemData.oneTimeThread.submit(new Runnable() {
            @Override
            public void run() {
                buttonLock.writeLock().lock();
                if (itemInDeck.get(isPlayer1)[position] == null
                        || currentState.get() != Id.GameState.PREPARE) {
                    buttonLock.writeLock().unlock();
                    return;
                }
                if (isPlayer1 || !isAi){
                    if (unitInDeck.get(isPlayer1)[position].cost.get() > cost.get(isPlayer1)) {
                        buttonLock.writeLock().unlock();
                        return;
                    }
                }
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
                    placeUnit(position);
                }
            });


            itemImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    useItem(position);
                }
            });
        }

        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemData.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        buttonLock.writeLock().lock();
                        currentState.set(Id.GameState.MOVING);

                        // set prepare visible
                        SystemData.postToUi(new Runnable() {
                            @Override
                            public void run() {
                                unitMenu.setVisibility(View.INVISIBLE);
                            }
                        });

                        buttonLock.writeLock().unlock();
                        prepareDoneLock.writeLock().lock();
                        synchronized (dataCondition) {
                            dataCondition.hasUserInput = true;
                            dataCondition.notify();
                        }

                        // recover cost
                        setCostBy(costPerTurn.get(isPlayer1));
                        postCost();
                        prepareDoneLock.writeLock().unlock();
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
                        buttonLock.writeLock().lock();
                        try{
                            if (cost.get(isPlayer1) >= DRAW_COST){
                                draw();
                                setCostBy(-DRAW_COST);
                                postCost();
                            }
                        } finally {
                            buttonLock.writeLock().unlock();
                        }

                    }
                });
            }
        });
    }


    public void screenFocusOn(GameObject gameObject) {
        int x = gameObject.x.get() - SystemData.getScreenWidth() / 2 + gameObject.getPortrait().getWidth() / 2;
        screenFocusOn(x);
    }

    public void screenFocusOn(int x) {
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


    public void animatePost(int start, int end, final AtomicInteger value, final TextView textView) {
        boolean isPositive = end >= start ? true : false;
        float delta = (float) Math.abs((end - start));
        float updateTimes = delta / VALUE_PER_SECOND  * ANIME_PER_SECOND;
        float cache = start;
        float increment = delta / updateTimes;

        while (isPositive ? cache < end : cache > end) {
            final int copy = (int) cache;
            value.set(copy);
            SystemData.postToUi(new Runnable() {
                @Override
                public void run() {
                    textView.setText(value.toString());
                }
            });

            cache = isPositive ? cache + increment : cache - increment;

            if (isPositive ? cache > end : cache < end) {
                cache = end;
            }
            try {
                Thread.sleep(ANIME_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        value.set(end);
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                textView.setText(value.toString());
            }
        });
    }


    public void animateMove(int start, int end, AtomicInteger value, GameObject gameObject) {
        boolean isPositive = end >= start;
        float delta = (float) Math.abs((end - start));
        float updateTimes = delta / PIXEL_PER_SECOND * LOGIC_PER_SECOND;
        float cache = start;
        float increment = delta / updateTimes;

        screenFocusOn(gameObject);

        while (isPositive ? cache < end : cache > end) {
            final int copy = (int) cache;
            value.set(copy);
            if (copy > screenView.getScrollX() + SystemData.getScreenWidth() * 0.9 ||
                    copy < screenView.getScrollX() + SystemData.getScreenWidth() * 0.1) screenFocusOn(gameObject);

            cache = isPositive ? cache + increment : cache - increment;

            if (isPositive ? cache > end : cache < end){
                cache = end;
            }
            try {
                Thread.sleep(LOGIC_SLEEP_TIME);
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
        boolean isPositive = end >= start;
        float delta = (float) Math.abs((end - start));
        float updateTimes = delta / VALUE_PER_SECOND * LOGIC_PER_SECOND;
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
                Thread.sleep(LOGIC_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // last time no need to sleep, no increment, set to end directly
        value.set(end);
    }


    public void onFirstStart(){
        this.isGameActive.set(true);
        waits.put(Id.Thread.SCREEN, SystemData.gameThreads.submit(updateScreen));
        waits.put(Id.Thread.ANIME, SystemData.gameThreads.submit(updateAnime));
        screenFocusOn(backgroundWidth / 2 - SystemData.getScreenWidth() / 2);
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                screenView.setVisibility(View.VISIBLE);
            }
        });
        waits.put(Id.Thread.DATA, SystemData.gameThreads.submit(updateData));
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
