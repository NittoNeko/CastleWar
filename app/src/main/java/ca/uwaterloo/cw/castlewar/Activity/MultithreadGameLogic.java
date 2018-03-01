package ca.uwaterloo.cw.castlewar.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
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
    public class ScreenCondition
    {
        public boolean finished = false;
    }
    // combat environment
    private class Combat {
        private final int MAX_TURN = 5;
        private int currentTurn;
        private HashMap<Boolean, Unit> units = new HashMap<>(2);
        private Boolean[] turns = new Boolean[MAX_TURN];
        private int distance;

        public Combat(Unit attacker, Unit defender) {
            this.currentTurn = 0;
            this.units.put(true, attacker);
            this.units.put(false, defender);
            this.distance = Math.abs(attacker.getCurrentTile().getParentId() - defender.getCurrentTile().getParentId());
            // initialize turns
            for (int i = 0; i < MAX_TURN; ++i) {
                turns[i] = i % 2 == 0;
            }
            if (attacker.speed.get() > defender.speed.get()) {
                turns[MAX_TURN - 1] = true;
            } else if (attacker.speed.get() > defender.speed.get()) {
                turns[MAX_TURN - 1] = false;
            } else {
                turns[MAX_TURN - 1] = null;
            }
        }

        // return result
        public boolean fight() {
            if (currentTurn >= MAX_TURN) return true;
            // update turn
            Boolean isAttacker = turns[currentTurn];
            this.currentTurn++;

            // execute turn
            if (isAttacker != null) {
                if (!isAttacker && defender instanceof Castle) return false;
                if (isAttacker && attacker instanceof Castle) return false;
                if (units.get(isAttacker).getMaxRange() < distance || units.get(isAttacker).getMinRange() > distance) return false;

                // take damage
                units.get(!isAttacker).takeDamage(units.get(isAttacker));
            }

            // check death or turn number
            if (attacker.isDead() || defender.isDead()) return true;
            return false;
        }
    }

    private class UpdateLogic implements Runnable {
        private long reaSleepTime = SystemClock.uptimeMillis();
        private Unit currentUnit = null;
        private ArrayList<ValueAnimator> anime = new ArrayList<>();
        private Combat combat = null;
        private HashMap<Boolean, HashMap<Id.CombatBoard, Integer>> before = new HashMap<>(2);
        private HashMap<Boolean, HashMap<Id.CombatBoard, Integer>> after = new HashMap<>(2);

        public void playAnime(final HashMap<Id.CombatBoard, TextView> info, final boolean isAttacker){
            for (final Id.CombatBoard iterator : Id.CombatBoard.values()) {
                if (iterator == Id.CombatBoard.NAME) continue;
                if (before.get(isAttacker).get(iterator).intValue() != after.get(isAttacker).get(iterator).intValue()) {
                    final Integer intBefore = before.get(isAttacker).get(iterator);
                    final Integer intAfter = after.get(isAttacker).get(iterator);
                    SystemData.postToUi(new Runnable() {
                        @Override
                        public void run() {
                            Integer distance = Math.abs(intBefore.intValue() - intAfter.intValue());
                            final ValueAnimator animator = ValueAnimator.ofInt(intBefore, intAfter);
                            long animeDuration = distance * SystemData.MILISECOND / SystemData.VALUE_PER_SECOND;
                            long delay = SystemData.MILISECOND / SystemData.VALUE_PER_SECOND;
                            animator.setDuration(animeDuration);
                            ValueAnimator.setFrameDelay(delay);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                                    final String string = valueAnimator.getAnimatedValue().toString();
                                    info.get(iterator).setText(string);
                                }
                            });
                            animator.start();
                            anime.add(animator);
                        }
                    });
                }
            }
        }

        public void record(HashMap<Boolean, HashMap<Id.CombatBoard, Integer>> result) {
            result.put(true, new HashMap<Id.CombatBoard, Integer>(Id.CombatBoard.values().length));
            result.put(false, new HashMap<Id.CombatBoard, Integer>(Id.CombatBoard.values().length));
            for (Id.CombatBoard iterator : Id.CombatBoard.values()) {
                if (iterator == Id.CombatBoard.NAME) {
                    result.get(true).put(iterator, 0);
                    result.get(false).put(iterator, 0);
                } else if (iterator == Id.CombatBoard.MAXHP) {
                    result.get(true).put(iterator, attacker.maxHp.get());
                    result.get(false).put(iterator, defender.maxHp.get());
                } else if (iterator == Id.CombatBoard.HP) {
                    result.get(true).put(iterator, attacker.hp.get());
                    result.get(false).put(iterator, defender.hp.get());
                } else if (iterator == Id.CombatBoard.ATTACK) {
                    result.get(true).put(iterator, attacker.attack.get());
                    result.get(false).put(iterator, defender.attack.get());
                } else if (iterator == Id.CombatBoard.DEFENSE) {
                    result.get(true).put(iterator, attacker.defense.get());
                    result.get(false).put(iterator, defender.defense.get());
                } else if (iterator == Id.CombatBoard.SPEED) {
                    result.get(true).put(iterator, attacker.speed.get());
                    result.get(false).put(iterator, defender.speed.get());
                }
            }
        }

        public Unit selectNextUnit() {
            Unit unit = null;
            unitInCombatLock.readLock().lock();
            for (Unit iterator : unitInCombat.get(isPlayer1.get())) {
                if (iterator.isReady()) {
                    unit = iterator;
                    break;
                }
            }
            unitInCombatLock.readLock().unlock();
            return unit;
        }

        public void updateLogic() {
            unitInCombatLock.readLock().lock();
            for (Unit iterator : unitInCombat.get(isPlayer1.get())) {
                iterator.move();
            }
            unitInCombatLock.readLock().unlock();
            if (currentState.get() == Id.GameState.PREPARE) {
                if (isAi && !isPlayer1.get()) {
                    if (SystemData.gameFlow) System.out.println("Ai'turn");
                    if (level.isInterval()) {
                        while (rearBattleField.get(isPlayer1.get()).getAvailableTileNum() > 0) {
                            try {
                                placeUnit(random.nextInt(level.getEnemies().length)).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (SystemData.gameFlow) System.out.println("summon enemy");
                        }
                    } else {
                        for (int i = 0; i < level.getMaxUnit(); ++i) {
                            if (rearBattleField.get(isPlayer1.get()).getAvailableTileNum() > 0) {
                                try {
                                    placeUnit(random.nextInt(level.getEnemies().length)).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                if (SystemData.gameFlow) System.out.println("summon enemy");
                            } else {
                                break;
                            }
                        }
                    }
                    // fight
                    goFight();
                } else {
                    if (SystemData.gameFlow) System.out.println("waiting for input");
                }
            } else if (currentState.get() == Id.GameState.MOVING) {
                if (currentUnit == null) {
                    if (SystemData.gameFlow) System.out.println("select unit");
                    currentUnit = selectNextUnit();
                    if (currentUnit == null) {
                        if (SystemData.gameFlow) System.out.println("no unit is ready, switch turn");
                        if (isAi) isPlayer1.set(!isPlayer1.get());
                        else switchPlayer();
                        screenFocusOn(castle.get(isPlayer1.get()));
                        postCost();
                        currentState.set(Id.GameState.PREPARE);

                        // reset isReady for units
                        unitInCombatLock.readLock().lock();
                        for (Unit unit : unitInCombat.get(isPlayer1.get())) {
                            unit.setReady(true);
                        }
                        unitInCombatLock.readLock().unlock();
                        // take turns
                        return;
                    } else {
                        if (SystemData.gameFlow) System.out.println("selected, decide strategy");
                        screenFocusOn(currentUnit);
                        if (currentUnit.getCurrentTile() == null){
                            currentUnit = null;
                            return;
                        } else {
                            currentUnit.decideStrategy(terrain);
                        }
                    }
                }
                if (currentUnit.getMoveTile() == null) {
                    if (SystemData.gameFlow) System.out.println("finish moving");
                    // go to combat
                    if (currentUnit.getActionTile() != null) {
                        if (SystemData.gameFlow) System.out.println("have action tile");
                        attacker = currentUnit.isPlayer1() ? currentUnit : currentUnit.getActionTile().getUnit();
                        defender = currentUnit.isPlayer1() ? currentUnit.getActionTile().getUnit() : currentUnit;
                        attacker.changeDirection(defender.getCurrentTile());
                        defender.changeDirection(attacker.getCurrentTile());
                        // targeting
                        if (!target.isVisible()) {
                            if (SystemData.gameFlow) System.out.println("start targeting");
                            target.x.set(currentUnit.x.get());
                            target.setVisible(true);
                            target.setMoveTile(currentUnit.getActionTile());
                        } else {
                            if (SystemData.gameFlow) System.out.println("targeting");
                            target.move();
                            screenFocusOn(target);
                            if (target.getMoveTile() == null) {
                                if (SystemData.gameFlow) System.out.println("finish targeting, switch to combat");
                                // before go to combat
                                target.setVisible(false);
                                combat = new Combat(attacker, defender);
                                SystemData.postToUi(new Runnable() {
                                    @Override
                                    public void run() {
                                        attackerInfo.get(Id.CombatBoard.NAME).setText(attacker.getName());
                                        attackerInfo.get(Id.CombatBoard.HP).setText(attacker.hp.toString());
                                        attackerInfo.get(Id.CombatBoard.MAXHP).setText(attacker.maxHp.toString());
                                        attackerInfo.get(Id.CombatBoard.ATTACK).setText(attacker.attack.toString());
                                        attackerInfo.get(Id.CombatBoard.DEFENSE).setText(attacker.defense.toString());
                                        attackerInfo.get(Id.CombatBoard.SPEED).setText(attacker.speed.toString());
                                        defenderInfo.get(Id.CombatBoard.NAME).setText(defender.getName());
                                        defenderInfo.get(Id.CombatBoard.HP).setText(defender.hp.toString());
                                        defenderInfo.get(Id.CombatBoard.MAXHP).setText(defender.maxHp.toString());
                                        defenderInfo.get(Id.CombatBoard.ATTACK).setText(defender.attack.toString());
                                        defenderInfo.get(Id.CombatBoard.DEFENSE).setText(defender.defense.toString());
                                        defenderInfo.get(Id.CombatBoard.SPEED).setText(defender.speed.toString());
                                    }
                                });
                                currentState.set(Id.GameState.COMBAT);
                            }
                        }
                    } else {
                        if (SystemData.gameFlow) System.out.println("no action tile, end this character's turn");
                        currentUnit.setReady(false);
                        currentUnit = null;
                    }
                } else {
                    if (SystemData.gameFlow) System.out.println("moving");
                    screenFocusOn(currentUnit);
                }
            } else if (currentState.get() == Id.GameState.COMBAT) {
                // if combat finished
                if (combat == null && anime.isEmpty()) {
                    if (SystemData.gameFlow) System.out.println("combat finished, end this character's turn");
                    currentUnit.setReady(false);
                    currentUnit = null;
                    currentState.set(Id.GameState.MOVING);
                } else{
                    if (anime.isEmpty()) {
                        if (SystemData.gameFlow) System.out.println("start combat");
                        // store before fight
                        record(before);

                        // cause delay between turns
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // fight and see if combat is done
                        if (combat.fight()) combat = null;

                        // store after fight
                        record(after);

                        // play anime for text view
                        playAnime(attackerInfo, true);
                        playAnime(defenderInfo, false);
                    } else {
                        if (SystemData.gameFlow) System.out.println("waiting for animation");
                        // check if running
                        Iterator<ValueAnimator> iterator = anime.iterator();
                        while (iterator.hasNext()){
                            ValueAnimator animator = iterator.next();
                            if (!animator.isRunning()) iterator.remove();
                        }
                    }
                }
            }

            // check death
            unitInCombatLock.writeLock().lock();
            Iterator<Unit> iterator = unitInCombat.get(true).iterator();
            while (iterator.hasNext()) {
                Unit holder = iterator.next();
                if (holder.isDead()) {
                    iterator.remove();
                }
            }
            iterator = unitInCombat.get(false).iterator();
            while (iterator.hasNext()) {
                Unit holder = iterator.next();
                if (holder.isDead()) {
                    iterator.remove();
                }
            }
            unitInCombatLock.writeLock().unlock();

            // check win
            if (castle.get(true).isDead()) {
                // add something in the future
                SystemData.postToUi(new Runnable() {
                    @Override
                    public void run() {
                        activity.onBackPressed();
                    }
                });
            } else if (castle.get(false).isDead()) {
                // add something
                SystemData.postToUi(new Runnable() {
                    @Override
                    public void run() {
                        activity.onBackPressed();
                    }
                });
            }
        }

        @Override
        public void run() {
            while (isGameActive.get()) {
                reaSleepTime = SystemClock.uptimeMillis() + SystemData.LOGIC_SLEEP_TIME;
                updateLogic();
                reaSleepTime -= SystemClock.uptimeMillis();
                if (reaSleepTime > 0){
                    try {
                        Thread.sleep(reaSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
                    Thread.sleep(SystemData.CONSTANT_SLEEP_TIME);
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
            canvas.drawBitmap(background, 0, backgroundY, paint);
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
                    //if (currentState.get() != Id.GameState.COMBAT)
                    gameScreen.setImageBitmap(screen);
                    // set visibility
                    if (currentState.get() == Id.GameState.PREPARE){
                        if (unitMenu.getVisibility() == View.GONE) {
                            if (isPlayer1.get() || !isAi){
                                unitMenu.setVisibility(View.VISIBLE);
                            }
                        }
                        if (combatBoard.getVisibility() == View.VISIBLE) combatBoard.setVisibility(View.GONE);
                    } else if (currentState.get() == Id.GameState.COMBAT) {
                        if (unitMenu.getVisibility() == View.VISIBLE) unitMenu.setVisibility(View.GONE);
                        if (combatBoard.getVisibility() == View.GONE) combatBoard.setVisibility(View.VISIBLE);
                    } else if (currentState.get() == Id.GameState.MOVING){
                        if (unitMenu.getVisibility() == View.VISIBLE) unitMenu.setVisibility(View.GONE);
                        if (combatBoard.getVisibility() == View.VISIBLE) combatBoard.setVisibility(View.GONE);
                    }

                    if (SystemData.gameFps) System.out.println("after ui set before wake: " + SystemClock.uptimeMillis());

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
                if (SystemData.gameFps) System.out.println("before wait after ui: " + SystemClock.uptimeMillis());
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
                realSleepTime -= SystemClock.uptimeMillis();
                if (SystemData.gameFps) System.out.println("real sleep time: " + realSleepTime);
                    if (realSleepTime > 0)
                {
                    // run ahead
                    // let's run faster
                    framePerSecond += 1;
                    if (framePerSecond >= SystemData.MAX_FPS) framePerSecond = SystemData.MAX_FPS;
                    screenSleepTime = SystemData.MILISECOND / (long) framePerSecond;

                    try {
                        Thread.sleep(realSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (realSleepTime < 0) {
                    // run below
                    // let's slow down
                    framePerSecond -= 1;
                    if (framePerSecond <= SystemData.MIN_FPS) framePerSecond = SystemData.MIN_FPS;
                    screenSleepTime = SystemData.MILISECOND / (long) framePerSecond;
                }

                if (SystemData.gameFps) System.out.println("FPS: " + framePerSecond);
            }
        }
    }

    // Game object
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
    private Boolean[] players = new Boolean[]{true, false};
    private UpdateLogic updateLogic = new UpdateLogic();
    private UpdateScreen updateScreen = new UpdateScreen();
    private UpdateAnime updateAnime = new UpdateAnime();
    private final int BASIC_RECOVERY = 3;
    private AtomicBoolean isGameActive = new AtomicBoolean();
    private AtomicState currentState = new AtomicState();
    private final ScreenCondition screenCondition = new ScreenCondition();
    private ReentrantReadWriteLock unitInCombatLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock buttonLock = new ReentrantReadWriteLock();
    private Random random = new Random();
    private boolean isAi;
    private AtomicBoolean isPlayer1 = new AtomicBoolean();
    private HashMap<Boolean, Integer> cost = new HashMap<>(2);
    private HashMap<Boolean, Integer> maxCost = new HashMap<>(2);
    private HashMap<Boolean, Integer> costPerTurn = new HashMap<>(2);
    private HashMap<Boolean, Terrain.BattleField> rearBattleField = new HashMap<>(2);

    // screen control
    private float framePerSecond;
    private long screenSleepTime;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final ImageView gameScreen;
    private final LinearLayout unitMenu;
    private final LinearLayout combatBoard;
    private HashMap<Id.CombatBoard, TextView> attackerInfo = new HashMap<>(Id.CombatBoard.values().length);
    private HashMap<Id.CombatBoard, TextView> defenderInfo = new HashMap<>(Id.CombatBoard.values().length);
    private final ImageButton[] unitImageButtons = new ImageButton[SystemData.CARD_NUM];
    private final ImageButton[] itemImageButtons = new ImageButton[SystemData.CARD_NUM];
    private final TextView[] unitCostTexts = new TextView[SystemData.CARD_NUM];
    private final TextView[] itemCostTexts = new TextView[SystemData.CARD_NUM];
    private final TextView costText;
    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap screen;
    private HorizontalScrollView screenView;
    private ProgressBar progressBar;
    private AtomicBoolean hasScroll = new AtomicBoolean();
    private Bitmap background;
    private int backgroundY;

    private MultithreadGameLogic(Activity activity, Terrain terrain)
    {
        this.background = SystemData.getRandomGameBackground(terrain.getBattleFieldsWidth());
        this.backgroundY = (SystemData.getScreenHeight() - background.getHeight()) / 2;
        this.hasScroll.set(true);
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
        for (int i = 0; i < SystemData.CARD_NUM; ++i){
            unitImageButtons[i] = (ImageButton) units.getChildAt(i);
            itemImageButtons[i] = (ImageButton) items.getChildAt(i);
            unitCostTexts[i] = (TextView) unitCost.getChildAt(i);
            itemCostTexts[i] = (TextView) itemCost.getChildAt(i);
        }
        this.costText = activity.findViewById(R.id.cost);
        this.attackerInfo.put(Id.CombatBoard.NAME, (TextView) activity.findViewById(R.id.AttackerName));
        this.attackerInfo.put(Id.CombatBoard.HP, (TextView) activity.findViewById(R.id.AttackerHp));
        this.attackerInfo.put(Id.CombatBoard.MAXHP, (TextView) activity.findViewById(R.id.AttackerMaxHp));
        this.attackerInfo.put(Id.CombatBoard.ATTACK, (TextView) activity.findViewById(R.id.AttackerAttack));
        this.attackerInfo.put(Id.CombatBoard.DEFENSE, (TextView) activity.findViewById(R.id.AttackerDefense));
        this.attackerInfo.put(Id.CombatBoard.SPEED, (TextView) activity.findViewById(R.id.AttackerSpeed));
        this.defenderInfo.put(Id.CombatBoard.NAME, (TextView) activity.findViewById(R.id.DefenderName));
        this.defenderInfo.put(Id.CombatBoard.HP, (TextView) activity.findViewById(R.id.DefenderHp));
        this.defenderInfo.put(Id.CombatBoard.MAXHP, (TextView) activity.findViewById(R.id.DefenderMaxHp));
        this.defenderInfo.put(Id.CombatBoard.ATTACK, (TextView) activity.findViewById(R.id.DefenderAttack));
        this.defenderInfo.put(Id.CombatBoard.DEFENSE, (TextView) activity.findViewById(R.id.DefenderDefense));
        this.defenderInfo.put(Id.CombatBoard.SPEED, (TextView) activity.findViewById(R.id.DefenderSpeed));
        this.rearBattleField.put(true, terrain.getBattleFields()[0]);
        this.rearBattleField.put(false, terrain.getBattleFields()[terrain.getBattleFieldNum() - 1]);
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = SystemData.getScreenHeight();
        this.screenSleepTime = SystemData.MILISECOND / (long) framePerSecond;
        this.castle.put(true, SystemData.createUnit(Id.Unit.HOLY_CASTLE.ordinal()));
        this.castle.put(false, SystemData.createUnit(Id.Unit.EVIL_CASTLE.ordinal()));
        this.castle.get(true).setPlayer1(true);
        this.castle.get(false).setPlayer1(false);
        this.castle.get(false).x.set(backgroundWidth - castle.get(false).getPortrait().getWidth());
        int leftCastlePosition = Castle.SIZE / SystemData.PIXEL / 2;
        int rightCastlePosition = Castle.SIZE / SystemData.PIXEL / 2 + terrain.getBattleFieldLength() / SystemData.PIXEL;
        for (int i = 0; i < Castle.SIZE / SystemData.PIXEL; ++i){
            this.rearBattleField.get(true).getTiles()[i].setUnit(castle.get(true));
            if (i == leftCastlePosition) this.castle.get(true).setCurrentTile(this.rearBattleField.get(true).getTiles()[i]);
        }
        for (int i = rearBattleField.get(false).getTiles().length - 1; i >= Castle.SIZE / SystemData.PIXEL / 2 + terrain.getBattleFieldLength() / SystemData.PIXEL; --i){
            this.rearBattleField.get(false).getTiles()[i].setUnit(castle.get(false));
            if (i == rightCastlePosition) this.castle.get(false).setCurrentTile(this.rearBattleField.get(false).getTiles()[i]);
        }
        this.costPerTurn.put(true, BASIC_RECOVERY);
        this.costPerTurn.put(false, BASIC_RECOVERY);
        initializeImages();

    }

    public MultithreadGameLogic(Activity activity, Level level, Unit[] unitInStockPlayer1, Item[] itemInStockPlayer1) {
        this(activity, level.getTerrain());
        this.isAi = true;
        this.level = level;
        this.unitInDeck.put(true, new Unit[SystemData.CARD_NUM]);
        this.unitInDeck.put(false, new Unit[level.getEnemies().length]);
        this.itemInDeck.put(true, new Item[SystemData.CARD_NUM]);
        this.itemInDeck.put(false, new Item[level.getItems().length]);
        this.unitInStock.put(true, unitInStockPlayer1);
        this.unitInStock.put(false, level.getEnemies());
        this.itemInStock.put(true, itemInStockPlayer1);
        this.itemInStock.put(false, itemInStockPlayer1);
        this.cost.put(true, UserProfile.getMaxCost());
        this.cost.put(false, UserProfile.getMaxCost());
        this.maxCost.put(true, UserProfile.getMaxCost());
        this.maxCost.put(false, UserProfile.getMaxCost());
        this.isPlayer1.set(true);
        for (int i = 0; i < SystemData.CARD_NUM; ++i){
            generateItemCard(i);
            generateUnitCard(i);
        }
        this.unitInDeck.put(false, level.getEnemies());
        this.itemInDeck.put(false, level.getItems());
        this.isPlayer1.set(false);
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
        for (int i = 0; i < SystemData.CARD_NUM; ++i){
            isPlayer1.set(true);
            generateItemCard(i);
            generateUnitCard(i);
            isPlayer1.set(false);
            generateUnitCard(i);
            generateItemCard(i);
        }
        initializeButtons();

        // remember to initialize unit in deck for player 2
    }

    public void initializeImages(){
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                combatBoard.setBackground(SystemData.scaleDrawable(R.drawable.combat_board, SystemData.getScreenWidth(), null, 4));
                unitMenu.setBackground(SystemData.scaleDrawable(R.drawable.plane_yellow, null, SystemData.getScreenHeight() / 2, 4));
                activity.findViewById(R.id.EndTurn).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,SystemData.PIXEL, 1));
                activity.findViewById(R.id.Redraw).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,SystemData.PIXEL, 1));
                activity.findViewById(R.id.Draw).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,SystemData.PIXEL, 1));
                activity.findViewById(R.id.cost).setBackground(SystemData.scaleDrawable(R.drawable.button_blue_long,null,SystemData.PIXEL, 4));
                for (ImageButton imageButton : unitImageButtons){
                    imageButton.setBackground(SystemData.scaleDrawable(R.drawable.square_blue_button, SystemData.PIXEL, SystemData.PIXEL, 1));
                }
                for (ImageButton imageButton : itemImageButtons){
                    imageButton.setBackground(SystemData.scaleDrawable(R.drawable.square_blue_button, SystemData.PIXEL, SystemData.PIXEL, 1));
                }
                for (TextView textView : unitCostTexts){
                    textView.setBackground(SystemData.scaleDrawable(R.drawable.button_blue_small, SystemData.PIXEL, SystemData.PIXEL, 4));
                }
                for (TextView textView : itemCostTexts){
                    textView.setBackground(SystemData.scaleDrawable(R.drawable.button_blue_small, SystemData.PIXEL, SystemData.PIXEL, 4));
                }
            }
        });
    }

    public void drawCards(){
        int countUnit = 0, countItem = 0;
        for (int i = 0; i < SystemData.CARD_NUM; ++i){
            if (unitInDeck.get(isPlayer1.get())[i] == null){
                if (countUnit < SystemData.DRAW_NUM){
                    generateUnitCard(i);
                    postUnitCard(i);
                    countUnit++;
                }
            }
            if (itemInDeck.get(isPlayer1.get())[i] == null){
                if (countItem < SystemData.DRAW_NUM){
                    generateItemCard(i);
                    postItemCard(i);
                    countItem++;
                }
            }
        }
    }


    public void goFight(){
        buttonLock.writeLock().lock();
        currentState.set(Id.GameState.MOVING);
        buttonLock.writeLock().unlock();

        // recover cost
        setCostBy(costPerTurn.get(isPlayer1.get()));
    }

    public void generateUnitCard(int position)
    {
        final Unit unit = SystemData.createUnit(unitInStock.get(isPlayer1.get())[random.nextInt(unitInStock.get(isPlayer1.get()).length)].getId());
        unitInDeck.get(isPlayer1.get())[position] = unit;
    }

    public void generateItemCard(int position)
    {
        final Item item = SystemData.createItem(itemInStock.get(isPlayer1.get())[random.nextInt(itemInStock.get(isPlayer1.get()).length)].getId());
        itemInDeck.get(isPlayer1.get())[position] = item;
    }

    public void postUnitCard(final int position) {
        final Unit unit = unitInDeck.get(isPlayer1.get())[position];
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                unitImageButtons[position].setImageBitmap(unit != null ? unit.getPortrait() : SystemData.getEmptyIcon());
                unitCostTexts[position].setText(unit != null ? unit.cost.toString() : "0");
            }
        });
    }
    public void postItemCard(final int position) {
        final Item item = itemInDeck.get(isPlayer1.get())[position];
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
                costText.setText(cost.get(isPlayer1.get()).toString() + "/" + maxCost.get(isPlayer1.get()).toString());
            }
        });
    }

    public void setCostBy(int delta){
        int currentCost = cost.get(isPlayer1.get()) + delta;
        if (currentCost > maxCost.get(isPlayer1.get())) currentCost = maxCost.get(isPlayer1.get());
        if (currentCost < 0) currentCost = 0;
        cost.put(isPlayer1.get(), currentCost);
    }

    public Future<?> placeUnit(final int position) {
        return SystemData.oneTimeThread.submit(new Runnable() {
            public void run() {
                buttonLock.writeLock().lock();
                if (unitInDeck.get(isPlayer1.get())[position] == null
                        || currentState.get() != Id.GameState.PREPARE) {
                    buttonLock.writeLock().unlock();
                    return;
                }
                if (isPlayer1.get() || !isAi) {
                    if (unitInDeck.get(isPlayer1.get())[position].cost.get() > cost.get(isPlayer1.get())) {
                        buttonLock.writeLock().unlock();
                        return;
                    }
                }
                if (rearBattleField.get(isPlayer1.get()).getAvailableTileNum() <= 0) return;
                Terrain.Tile tile = rearBattleField.get(isPlayer1.get()).findFirstAvailableTile(isPlayer1.get());
                Unit unit = SystemData.createUnit(unitInDeck.get(isPlayer1.get())[position].getId());
                if (unit == null) return;
                unit.x.set(castle.get(isPlayer1.get()).getCurrentTile().getX());
                unit.setPlayer1(isPlayer1.get());
                unit.setLeft(!isPlayer1.get());
                unit.setMoveTile(tile);
                tile.setUnit(unit);
                if (!isAi || isPlayer1.get()) {
                    unitInDeck.get(isPlayer1.get())[position] = null;
                    postUnitCard(position);
                    setCostBy(-unit.cost.get());
                    postCost();
                }
                unitInCombatLock.writeLock().lock();
                unitInCombat.get(isPlayer1.get()).add(unit);
                unitInCombatLock.writeLock().unlock();
                buttonLock.writeLock().unlock();
            }
        });
    }

    public Future<?> useItem(final int position){
        return SystemData.oneTimeThread.submit(new Runnable() {
            @Override
            public void run() {
                buttonLock.writeLock().lock();
                if (itemInDeck.get(isPlayer1.get())[position] == null
                        || currentState.get() != Id.GameState.PREPARE) {
                    buttonLock.writeLock().unlock();
                    return;
                }
                if (isPlayer1.get() || !isAi){
                    if (unitInDeck.get(isPlayer1.get())[position].cost.get() > cost.get(isPlayer1.get())) {
                        buttonLock.writeLock().unlock();
                        return;
                    }
                }
                Item item = itemInDeck.get(isPlayer1.get())[position];
                if (!isAi || isPlayer1.get()) {
                    itemInDeck.get(isPlayer1.get())[position] = null;
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
        isPlayer1.set(!isPlayer1.get());
        for (int i = 0; i < SystemData.CARD_NUM; ++i){
            postUnitCard(i);
            postItemCard(i);
        }
        postCost();
    }

    public void initializeButtons() {
        Button endTurn = activity.findViewById(R.id.EndTurn);
        Button redraw = activity.findViewById(R.id.Redraw);
        final Button draw = activity.findViewById(R.id.Draw);

        for (int i = 0; i < SystemData.CARD_NUM; ++i) {
            final int position = i;
            unitImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rearBattleField.get(isPlayer1.get()).getAvailableTileNum() > 0){
                        placeUnit(position);
                    }
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
                        goFight();
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
                            if (cost.get(isPlayer1.get()) >= SystemData.DRAW_COST){
                                drawCards();
                                setCostBy(-SystemData.DRAW_COST);
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
        if (!hasScroll.get()) return;
        if (x < 0) x = 0;
        else if (x > backgroundWidth - SystemData.getScreenWidth()) x = backgroundWidth - SystemData.getScreenWidth();
        final int copy = x;
        final float scrollTime = Math.abs(screenView.getScrollX() - copy) * SystemData.MILISECOND / SystemData.SCROLL_PIXEL_PER_SECOND;
        if (Math.abs(screenView.getScrollX() - copy) < SystemData.getScreenWidth() * 0.1) return;
        hasScroll.set(false);
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anime = ObjectAnimator.ofInt(screenView, "scrollX",copy);
                anime.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hasScroll.set(true);
                    }
                });
                anime.setDuration((int) scrollTime);
                anime.start();
            }
        });
    }

    public void onFirstStart(){
        this.isGameActive.set(true);
        currentState.set(Id.GameState.MOVING);
        waits.put(Id.Thread.SCREEN, SystemData.gameThreads.submit(updateScreen));
        waits.put(Id.Thread.ANIME, SystemData.gameThreads.submit(updateAnime));
        screenFocusOn(backgroundWidth / 2 - SystemData.getScreenWidth() / 2);
        // get enough time to initialize screen
        // just for user experience
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SystemData.postToUi(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                screenView.setVisibility(View.VISIBLE);
            }
        });
        currentState.set(Id.GameState.PREPARE);
        waits.put(Id.Thread.DATA, SystemData.gameThreads.submit(updateLogic));
        screenFocusOn(castle.get(true));
    }

    public void onResume()
    {
        this.isGameActive.set(true);
        waits.put(Id.Thread.DATA, SystemData.gameThreads.submit(updateLogic));
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
