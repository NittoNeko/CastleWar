package ca.uwaterloo.cw.castlewar.Game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ca.uwaterloo.cw.castlewar.Base.Animation;
import ca.uwaterloo.cw.castlewar.Base.Icon;
import ca.uwaterloo.cw.castlewar.Base.Sprite;
import ca.uwaterloo.cw.castlewar.Base.Tile;
import ca.uwaterloo.cw.castlewar.Structure.Atomic;
import ca.uwaterloo.cw.castlewar.Base.GameObject;
import ca.uwaterloo.cw.castlewar.Structure.CombatView;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Unit.Castle;
import ca.uwaterloo.cw.castlewar.Item.Item;
import ca.uwaterloo.cw.castlewar.Base.User;
import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Unit.Unit;

import static com.facebook.FacebookSdk.getCacheDir;


/**
 * Created by harrison33 on 2018/2/19.
 */

public class GameManager {
    // store self as public blackboard
    private static GameManager instance;
    public final Activity activity;

    private static final long MAX_FPS = 45;
    private static final long MIN_FPS = 5;
    private static final int BASIC_RECOVERY = 3;
    private static final int MILISECOND = 1000;
    private static final int CARD_NUM = 5;
    private static final int DRAW_NUM = 2;
    private static final int DRAW_COST = 1;
    private static final int MAX_COST = 5;
    private static final float SCROLL_PIXEL_PER_SECOND = 2000; // speed of screen
    private static AtomicInteger LOGIC_PER_SECOND = new AtomicInteger(25);  // this is the speed of updates of normal background thread
    private static final float CONSTANT_PER_SECOND = 10; // this is the speed of updates constant anime, music and so on
    private static AtomicInteger LOGIC_SLEEP_TIME = new AtomicInteger(MILISECOND / LOGIC_PER_SECOND.get());
    private static final long CONSTANT_SLEEP_TIME = MILISECOND / (long) CONSTANT_PER_SECOND;

    // Game object
    private Unit currentUnit = null;
    private Atomic.List<Animation> animations = new Atomic.List<>();
    private Combat combat = null;
    private HashMap<Id.Thread, Future<?>> waits = new HashMap<>(Id.Thread.values().length);
    private HashMap<Id.Player, Unit[]> unitInDeck = new HashMap<>(2);
    private HashMap<Id.Player, Item[]> itemInDeck = new HashMap<>(2);
    private HashMap<Id.Player, ArrayList<Unit>> unitInStock = new HashMap<>(2);
    private HashMap<Id.Player, ArrayList<Item>> itemInStock = new HashMap<>(2);
    private HashMap<Id.Player, Atomic.List<Unit>> unitInCombat = new HashMap<>(2);
    private HashMap<Id.Player, Unit> castle = new HashMap<>(2);
    private Unit attacker;
    private Unit defender;
    private Sprite.Target target = new Sprite.Target();
    private Level level;
    private Terrain terrain;

    // game control
    private AtomicBoolean doubleSpeed = new AtomicBoolean(false);
    private AtomicBoolean hasLine = new AtomicBoolean(false);
    private GameLogic gameLogic = new GameLogic();
    private GameScreen gameScreen = new GameScreen();
    private GameConstant gameConstant = new GameConstant();
    private AtomicBoolean isGameActive = new AtomicBoolean();
    private Atomic.Id<Id.GameState> currentState = new Atomic.Id<>(Id.GameState.PREPARE);
    private final ScreenCondition screenCondition = new ScreenCondition();
    private ReentrantReadWriteLock buttonLock = new ReentrantReadWriteLock();
    private Random random = new Random();
    private boolean isAi;
    private Id.Player player = Id.Player.ONE;
    private HashMap<Id.Player, Integer> cost = new HashMap<>(2);
    private HashMap<Id.Player, Integer> maxCost = new HashMap<>(2);
    private HashMap<Id.Player, Integer> costPerTurn = new HashMap<>(2);
    private HashMap<Id.Player, Terrain.BattleField> rearBattleField = new HashMap<>(2);

    // screen control
    private TextView winLose;
    private TextView coinReward;
    private float framePerSecond;
    private long screenSleepTime;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final ImageView gameScreenView;
    private final LinearLayout unitMenu;
    private final LinearLayout combatBoard;
    private CombatView attackerView;
    private CombatView defenderView;
    private final ImageButton[] unitImageButtons = new ImageButton[CARD_NUM];
    private final ImageButton[] itemImageButtons = new ImageButton[CARD_NUM];
    private final TextView[] unitCostTexts = new TextView[CARD_NUM];
    private final TextView[] itemCostTexts = new TextView[CARD_NUM];
    private final TextView costText;
    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap screen;
    private HorizontalScrollView screenView;
    private ImageView gameLoading;
    private AtomicBoolean hasScroll = new AtomicBoolean();
    private Bitmap background;
    private int backgroundY;

    private GameManager(Activity activity, Terrain terrain) {
        instance = this;
        this.coinReward = activity.findViewById(R.id.CoinReward);
        this.winLose = activity.findViewById(R.id.WinLose);
        this.attackerView = new CombatView(
                (TextView) activity.findViewById(R.id.AttackerHp),
                (TextView) activity.findViewById(R.id.AttackerMaxHp),
                (HorizontalScrollView) activity.findViewById(R.id.AttackerEffects),
                (ProgressBar) activity.findViewById(R.id.AttackerHpBar),
                (ImageView) activity.findViewById(R.id.AttackerImage),
                (ImageView) activity.findViewById(R.id.AttackerAnimation),
                (TextView) activity.findViewById(R.id.AttackerAbility));
        this.defenderView = new CombatView(
                (TextView) activity.findViewById(R.id.DefenderHp),
                (TextView) activity.findViewById(R.id.DefenderMaxHp),
                (HorizontalScrollView) activity.findViewById(R.id.DefenderEffects),
                (ProgressBar) activity.findViewById(R.id.DefenderHpBar),
                (ImageView) activity.findViewById(R.id.DefenderImage),
                (ImageView) activity.findViewById(R.id.DefenderAnimation),
                (TextView) activity.findViewById(R.id.DefenderAbility));
        this.background = System.getRandomGameBackground(terrain.getBattleFieldsWidth());
        this.backgroundY = (System.getScreenHeight() - background.getHeight()) / 2;
        this.hasScroll.set(true);
        this.gameLoading = activity.findViewById(R.id.GameLoading);
        this.paint.setARGB(255, 0, 0, 0);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(10);
        this.paint.setPathEffect(new DashPathEffect(new float[]{30, 10}, 0));
        this.screenView = activity.findViewById(R.id.GameScreenWrapper);
        this.attacker = null;
        this.defender = null;
        this.currentState.set(Id.GameState.PREPARE);
        this.activity = activity;
        for (Id.Thread id : Id.Thread.values())
            this.waits.put(id, null);
        this.unitInCombat.put(Id.Player.ONE, new Atomic.List<Unit>(25));
        this.unitInCombat.put(Id.Player.TWO, new Atomic.List<Unit>(25));
        this.framePerSecond = 30;
        this.isGameActive.set(false);
        this.terrain = terrain;
        this.gameScreenView = activity.findViewById(R.id.GameScreen);
        this.unitMenu = activity.findViewById(R.id.UnitMenu);
        this.combatBoard = activity.findViewById(R.id.CombatBoard);
        LinearLayout units = activity.findViewById(R.id.Units);
        LinearLayout items = activity.findViewById(R.id.Items);
        LinearLayout unitCost = activity.findViewById(R.id.UnitCost);
        LinearLayout itemCost = activity.findViewById(R.id.ItemCost);
        for (int i = 0; i < CARD_NUM; ++i) {
            unitImageButtons[i] = (ImageButton) units.getChildAt(i);
            itemImageButtons[i] = (ImageButton) items.getChildAt(i);
            unitCostTexts[i] = (TextView) unitCost.getChildAt(i);
            itemCostTexts[i] = (TextView) itemCost.getChildAt(i);
        }
        this.costText = activity.findViewById(R.id.cost);
        this.rearBattleField.put(Id.Player.ONE, terrain.getBattleFields()[0]);
        this.rearBattleField.put(Id.Player.TWO, terrain.getBattleFields()[terrain.getBattleFieldNum() - 1]);
        this.backgroundWidth = terrain.getBattleFieldsWidth();
        this.backgroundHeight = System.getScreenHeight();
        this.screenSleepTime = MILISECOND / (long) framePerSecond;
        this.castle.put(Id.Player.ONE, Castle.createCastle(Id.Castle.HOLY_CASTLE));
        this.castle.put(Id.Player.TWO, Castle.createCastle(Id.Castle.EVIL_CASTLE));
        this.castle.get(Id.Player.ONE).setPlayer(Id.Player.ONE);
        this.castle.get(Id.Player.TWO).setPlayer(Id.Player.TWO);
        this.castle.get(Id.Player.TWO).getSprite().setX(backgroundWidth - castle.get(Id.Player.TWO).getSprite().getPortrait().getWidth());
        int leftCastlePosition = Castle.SIZE / Tile.SIZE / 2;
        int rightCastlePosition = Castle.SIZE / Tile.SIZE / 2 + terrain.getBattleFieldLength() / Tile.SIZE;
        for (int i = 0; i < Castle.SIZE / Tile.SIZE; ++i) {
            this.rearBattleField.get(Id.Player.ONE).getTiles()[i].setUnit(castle.get(Id.Player.ONE));
            if (i == leftCastlePosition)
                this.castle.get(Id.Player.ONE).setCurrentTile(this.rearBattleField.get(Id.Player.ONE).getTiles()[i]);
        }
        for (int i = rearBattleField.get(Id.Player.TWO).getTiles().length - 1; i >= Castle.SIZE / Tile.SIZE / 2 + terrain.getBattleFieldLength() / Tile.SIZE; --i) {
            this.rearBattleField.get(Id.Player.TWO).getTiles()[i].setUnit(castle.get(Id.Player.TWO));
            if (i == rightCastlePosition)
                this.castle.get(Id.Player.TWO).setCurrentTile(this.rearBattleField.get(Id.Player.TWO).getTiles()[i]);
        }
        this.costPerTurn.put(Id.Player.ONE, BASIC_RECOVERY);
        this.costPerTurn.put(Id.Player.TWO, BASIC_RECOVERY);
        this.unitInDeck.put(Id.Player.ONE, new Unit[CARD_NUM]);
        this.unitInDeck.put(Id.Player.TWO, new Unit[CARD_NUM]);
        this.itemInDeck.put(Id.Player.ONE, new Item[CARD_NUM]);
        this.itemInDeck.put(Id.Player.TWO, new Item[CARD_NUM]);
        this.cost.put(Id.Player.ONE, MAX_COST);
        this.cost.put(Id.Player.TWO, MAX_COST);
        this.maxCost.put(Id.Player.ONE, MAX_COST);
        this.maxCost.put(Id.Player.TWO, MAX_COST);
        initializeImages();
    }

    // called for singleplayer
    public GameManager(Activity activity, Level level) {
        this(activity, level.getTerrain());
        this.isAi = true;
        this.level = level;
        this.unitInStock.put(Id.Player.ONE, User.currentLawfuls());
        this.unitInStock.put(Id.Player.TWO, level.currentChaotics());
        this.itemInStock.put(Id.Player.ONE, User.currentPotions());
        this.itemInStock.put(Id.Player.TWO, level.currentPotions());
        initializeUnitImage();
        initializeButtons();
    }

    // called for multiplayer
    public GameManager(Activity activity, Terrain terrain, Id.Castle player1, Id.Castle player2) {
        this(activity, terrain);
        this.isAi = false;
        this.terrain = terrain;
        if (player1 == Id.Castle.HOLY_CASTLE) {
            this.unitInStock.put(Id.Player.ONE, User.currentLawfuls());
        } else {
            this.unitInStock.put(Id.Player.ONE, User.currentChaotics());
        }
        if (player2 == Id.Castle.HOLY_CASTLE) {
            this.unitInStock.put(Id.Player.TWO, User.currentLawfuls());
        } else {
            this.unitInStock.put(Id.Player.TWO, User.currentChaotics());
        }
        this.itemInStock.put(Id.Player.ONE, User.currentPotions());
        this.itemInStock.put(Id.Player.TWO, User.currentPotions());
        initializeUnitImage();
        initializeButtons();
    }

    public void initializeUnitImage() {
        for (Unit unit : unitInStock.get(Id.Player.ONE)) {
            unit.getSprite().initializeAll();
        }
        for (Unit unit : unitInStock.get(Id.Player.TWO)) {
            unit.getSprite().initializeAll();
        }
    }

    private void initializeImages() {
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                activity.findViewById(R.id.AttackerHpBar).setBackground(System.scaleDrawable(R.drawable.health_bar, null, 100, 4));
                activity.findViewById(R.id.DefenderHpBar).setBackground(System.scaleDrawable(R.drawable.health_bar, null, 100, 4));
                ((ImageButton) activity.findViewById(R.id.SpeedButton)).setImageBitmap(System.scaleBitmap(R.drawable.speed_button, 200, 200, 3));
                ((ImageButton) activity.findViewById(R.id.LineButton)).setImageBitmap(System.scaleBitmap(R.drawable.line_button, 200, 200, 3));
                activity.findViewById(R.id.Result).setBackground(System.scaleDrawable(R.drawable.plane_yellow, 600, 600, 4));
                ((ImageButton) activity.findViewById(R.id.Facebook)).setImageBitmap(System.scaleBitmap(R.drawable.facebook, 100, 100, 2));
                ((ImageButton) activity.findViewById(R.id.GoBackToTitle)).setImageBitmap(System.scaleBitmap(R.drawable.ok, 100, 100, 2));
                unitMenu.setBackground(System.scaleDrawable(R.drawable.plane_yellow, null, System.getScreenHeight() / 2, 2));
                activity.findViewById(R.id.EndTurn).setBackground(System.scaleDrawable(R.drawable.blue_button, null, Tile.SIZE, 1));
                activity.findViewById(R.id.Redraw).setBackground(System.scaleDrawable(R.drawable.blue_button, null, Tile.SIZE, 1));
                activity.findViewById(R.id.Draw).setBackground(System.scaleDrawable(R.drawable.blue_button, null, Tile.SIZE, 1));
                activity.findViewById(R.id.cost).setBackground(System.scaleDrawable(R.drawable.button_blue_long, null, Tile.SIZE, 2));
                for (ImageButton imageButton : unitImageButtons) {
                    imageButton.setBackground(System.scaleDrawable(R.drawable.square_blue_button, Tile.SIZE, Tile.SIZE, 1));
                }
                for (ImageButton imageButton : itemImageButtons) {
                    imageButton.setBackground(System.scaleDrawable(R.drawable.square_blue_button, Tile.SIZE, Tile.SIZE, 1));
                }
                for (TextView textView : unitCostTexts) {
                    textView.setBackground(System.scaleDrawable(R.drawable.button_blue_small, Tile.SIZE, Tile.SIZE, 2));
                }
                for (TextView textView : itemCostTexts) {
                    textView.setBackground(System.scaleDrawable(R.drawable.button_blue_small, Tile.SIZE, Tile.SIZE, 2));
                }
            }
        });
    }

    private void redrawCards() {
        int countUnit = 0, countItem = 0;
        for (int i = 0; i < CARD_NUM; ++i) {
            if (unitInDeck.get(player)[i] != null) {
                if (countUnit < DRAW_NUM) {
                    generateUnitCard(i);
                    postUnitCard(i);
                    countUnit++;
                }
            }
            if (itemInDeck.get(player)[i] != null) {
                if (countItem < DRAW_NUM) {
                    generateItemCard(i);
                    postItemCard(i);
                    countItem++;
                }
            }
        }
    }

    private void drawCards() {
        int countUnit = 0, countItem = 0;
        for (int i = 0; i < CARD_NUM; ++i) {
            if (unitInDeck.get(player)[i] == null) {
                if (countUnit < DRAW_NUM) {
                    generateUnitCard(i);
                    postUnitCard(i);
                    countUnit++;
                }
            }
            if (itemInDeck.get(player)[i] == null) {
                if (countItem < DRAW_NUM) {
                    generateItemCard(i);
                    postItemCard(i);
                    countItem++;
                }
            }
        }
    }


    public void goFight() {
        currentState.set(Id.GameState.MOVING);

        // recover cost
        setCostBy(costPerTurn.get(player));
    }

    public void generateUnitCard(int position) {
        final Unit unit;
        int randomPos = random.nextInt(unitInStock.get(player).size());
        Unit chosen = unitInStock.get(player).get(randomPos);
        try {
            unit = chosen.getClass().newInstance();
            unit.clone(chosen);
            unitInDeck.get(player)[position] = unit;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateItemCard(int position) {
        final Item item;
        int randomPos = random.nextInt(itemInStock.get(player).size());
        Item chosen = itemInStock.get(player).get(randomPos);
        try {
            item = chosen.getClass().newInstance();
            item.clone(chosen);
            itemInDeck.get(player)[position] = item;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void postUnitCard(final int position) {
        final Unit unit = unitInDeck.get(player)[position];
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                unitImageButtons[position].setImageBitmap(unit != null ? unit.getPortrait() : Icon.empty);
                unitCostTexts[position].setText(unit != null ? Integer.toString(unit.getModifiedStatus().getCost()) : "0");
            }
        });
    }

    public void postItemCard(final int position) {
        final Item item = itemInDeck.get(player)[position];
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                itemImageButtons[position].setImageBitmap(item != null ? item.getPortrait() : Icon.empty);
                itemCostTexts[position].setText(item != null ? Integer.toString(item.getModifiedStatus().getCost()) : "0");
            }
        });
    }

    public void postCost() {
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                String string = (player == Id.Player.ONE ? "Player 1: " : "Player 2: ") + cost.get(player).toString() + "/" + maxCost.get(player).toString();
                costText.setText(string);
            }
        });
    }

    public void setCostBy(int delta) {
        int currentCost = cost.get(player) + delta;
        if (currentCost > maxCost.get(player)) currentCost = maxCost.get(player);
        if (currentCost < 0) currentCost = 0;
        cost.put(player, currentCost);
    }

    public void placeUnit(final int position) {
        Tile tile = rearBattleField.get(player).findFirstAvailableTile(player);
        Unit unit = unitInDeck.get(player)[position];
        unit.getSprite().setX(castle.get(player).getCurrentTile().getX());
        unit.setPlayer(player);
        unit.getSprite().setDirection(player == Id.Player.ONE ? Id.Direction.RIGHT : Id.Direction.LEFT);
        unit.setMoveTile(tile);
        tile.setUnit(unit);
        unitInDeck.get(player)[position] = null;
        unitInCombat.get(player).add(unit);
    }

    public void useItem(final int position) {
        Item item = itemInDeck.get(player)[position];
        itemInDeck.get(player)[position] = null;
    }

    // importatn when switching players
    public void switchPlayer() {
        player = player.getOpponent();
        if (player == Id.Player.ONE || !isAi) {
            for (int i = 0; i < CARD_NUM; ++i) {
                postUnitCard(i);
                postItemCard(i);
            }
            postCost();
        }
        // check buffs
        for (Unit unit : unitInCombat.get(player).getCopyOfContent()) {
            unit.checkEffect();
        }
    }

    public void initializeButtons() {
        // generate units and post on views
        for (int i = 0; i < CARD_NUM; ++i) {
            this.player = Id.Player.ONE;
            generateItemCard(i);
            generateUnitCard(i);
            postUnitCard(i);
            postItemCard(i);
            this.player = Id.Player.TWO;
            generateItemCard(i);
            generateUnitCard(i);
        }
        this.player = Id.Player.ONE;
        postCost();

        Button endTurn = activity.findViewById(R.id.EndTurn);
        Button redraw = activity.findViewById(R.id.Redraw);
        Button draw = activity.findViewById(R.id.Draw);
        ImageButton speed = activity.findViewById(R.id.SpeedButton);
        ImageButton line = activity.findViewById(R.id.LineButton);
        ImageButton facebook = activity.findViewById(R.id.Facebook);
        ImageButton ok = activity.findViewById(R.id.GoBackToTitle);

        for (int i = 0; i < CARD_NUM; ++i) {
            final int position = i;
            unitImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.oneTimeThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            buttonLock.writeLock().lock();
                            Unit unit = unitInDeck.get(player)[position];
                            if (unit == null) {
                                // cannot go for null unit
                            } else if (currentState.get() != Id.GameState.PREPARE) {
                                // cannot generate when it's not prepae statge
                            } else if (unit.getModifiedStatus().getCost() > cost.get(player)) {
                                // no enough cost
                            } else if (rearBattleField.get(player).getAvailableTileNum() <= 0) {
                                // no enough slot
                            } else {
                                placeUnit(position);
                                postUnitCard(position);
                                setCostBy(-unit.getModifiedStatus().getCost());
                                postCost();
                            }
                            buttonLock.writeLock().unlock();
                        }
                    });
                }
            });


            itemImageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.oneTimeThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            buttonLock.writeLock().lock();
                            Item item = itemInDeck.get(player)[position];
                            if (item == null) {
                                // cannot go for null unit
                            } else if (currentState.get() != Id.GameState.PREPARE) {
                                // cannot generate when it's not prepae statge
                            } else if (item.getModifiedStatus().getCost() > cost.get(player)) {
                                // no enough cost
                            } else {
                                useItem(position);
                                postUnitCard(position);
                                setCostBy(-item.getModifiedStatus().getCost());
                                postCost();
                            }
                            buttonLock.writeLock().unlock();
                        }
                    });
                }
            });
        }

        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        buttonLock.writeLock().lock();
                        goFight();
                        buttonLock.writeLock().unlock();
                    }
                });
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        buttonLock.writeLock().lock();
                        try {
                            if (cost.get(player) >= DRAW_COST) {
                                drawCards();
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

        redraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        buttonLock.writeLock().lock();
                        try {
                            if (cost.get(player) >= DRAW_COST) {
                                redrawCards();
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

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        doubleSpeed.set(!doubleSpeed.get());
                        if (doubleSpeed.get()) {
                            LOGIC_PER_SECOND.set(LOGIC_PER_SECOND.get() * 2);
                        } else {
                            LOGIC_PER_SECOND.set(LOGIC_PER_SECOND.get() / 2);
                        }
                        LOGIC_SLEEP_TIME.set(MILISECOND / LOGIC_PER_SECOND.get());
                        Animation.setSpeed(doubleSpeed.get());
                    }
                });
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.oneTimeThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        hasLine.set(!hasLine.get());
                    }
                });
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        activity.onBackPressed();
                    }
                });
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialog shareDialog = new ShareDialog(activity);
                Uri videoFileUri = Uri.parse("file://" + getCacheDir().getAbsolutePath() + "/gameplay.mp4");
                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(videoFileUri)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setContentTitle("My combat Videos")
                        .setContentDescription("Time to show my true skill")
                        .setVideo(video)
                        .build();
                if(shareDialog.canShow(ShareVideoContent.class)){
                    shareDialog.show(content);
            }
        });
    }


    public void screenFocusOn(GameObject gameObject) {
        screenFocusOn(gameObject.getSprite());
    }

    public void screenFocusOn(Sprite sprite) {
        int x = sprite.getX() - System.getScreenWidth() / 2 + sprite.getBitmap().getWidth() / 2;
        screenFocusOn(x);
    }

    public void screenFocusOn(int x) {
        if (!hasScroll.get()) return;
        if (x < 0) x = 0;
        else if (x > backgroundWidth - System.getScreenWidth())
            x = backgroundWidth - System.getScreenWidth();
        final int copy = x;
        final float scrollTime = Math.abs(screenView.getScrollX() - copy) * MILISECOND / SCROLL_PIXEL_PER_SECOND;
        if (Math.abs(screenView.getScrollX() - copy) < System.getScreenWidth() * 0.3) return;
        hasScroll.set(false);
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anime = ObjectAnimator.ofInt(screenView, "scrollX", copy);
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

    public void onFirstStart() {
        this.isGameActive.set(true);
        currentState.set(Id.GameState.MOVING);
        waits.put(Id.Thread.SCREEN, System.gameThreads.submit(gameScreen));
        waits.put(Id.Thread.ANIME, System.gameThreads.submit(gameConstant));
        screenFocusOn(backgroundWidth / 2 - System.getScreenWidth() / 2);
        // get enough time to initialize screen
        // just for user experience
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                gameLoading.setVisibility(View.GONE);
                screenView.setVisibility(View.VISIBLE);
                activity.findViewById(R.id.SpeedButton).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.LineButton).setVisibility(View.VISIBLE);

            }
        });
        currentState.set(Id.GameState.PREPARE);
        waits.put(Id.Thread.DATA, System.gameThreads.submit(gameLogic));
        screenFocusOn(castle.get(Id.Player.ONE));
    }

    public Combat getCurrentCombat() {
        return this.combat;
    }

    public void addAnimation(Animation animation) {
        this.animations.add(animation);
    }

    public void removeAnimation(Animation animation) {
        this.animations.remove(animation);
    }

    public boolean isAnimationEmpty() {
        return this.animations.isEmpty();
    }

    public boolean isGameActive() {
        return this.isGameActive.get();
    }

    public static GameManager instance() {
        if (instance == null) Log.e("NULL EXCEPTION", "NO ACTIVE GAME MANAGER", new NullPointerException());
        return instance;
    }

    public void onResume() {
        this.isGameActive.set(true);
        waits.put(Id.Thread.DATA, System.gameThreads.submit(gameLogic));
        waits.put(Id.Thread.SCREEN, System.gameThreads.submit(gameScreen));
        waits.put(Id.Thread.ANIME, System.gameThreads.submit(gameConstant));
    }

    public void onPause() {
        isGameActive.set(false);
        synchronized (screenCondition) {
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

    // simple condition lock
    public class ScreenCondition {
        public boolean finished = false;
    }

    private class GameLogic implements Runnable {
        private long reaSleepTime = SystemClock.uptimeMillis();

        public Unit selectNextUnit() {
            Unit unit = null;
            for (Unit iterator : unitInCombat.get(player).getCopyOfContent()) {
                if (iterator.isReady()) {
                    unit = iterator;
                    break;

                }
            }
            return unit;
        }

        public void gameLogic() {
            for (Unit iterator : unitInCombat.get(player).getCopyOfContent()) {
                iterator.move();
            }
            if (target.isVisible()) {
                target.move();
                screenFocusOn(target);
            }

            if (!animations.isEmpty()) {
                if (System.gameFlow) java.lang.System.out.println("waiting for animation");
                // check if running
                return;
            }

            // check death
            for (Unit unit : unitInCombat.get(Id.Player.ONE).getCopyOfContent()) {
                if (unit.isDead()) unitInCombat.get(Id.Player.ONE).remove(unit);
            }

            for (Unit unit : unitInCombat.get(Id.Player.TWO).getCopyOfContent()) {
                if (unit.isDead()) unitInCombat.get(Id.Player.TWO).remove(unit);
            }

            // check win
            if (castle.get(Id.Player.ONE).isDead()) {
                // add something in the future
                currentState.set(Id.GameState.MOVING);
                System.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        String string;
                        if (isAi) {
                            string = "You Lose!";
                            winLose.setText(string);
                            string = "You Gain Nothing!";
                            coinReward.setText(string);
                        } else {
                            string = "Player 2 Wins!";
                            winLose.setText(string);
                            coinReward.setText(null);
                        }
                        activity.findViewById(R.id.Result).setVisibility(View.VISIBLE);
                    }
                });
                while (isGameActive.get()) {
                    // busy wait until game ends
                }
                return;
            } else if (castle.get(Id.Player.TWO).isDead()) {
                currentState.set(Id.GameState.MOVING);
                System.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        String string;
                        if (isAi) {
                            string = "You Win!";
                            winLose.setText(string);
                            string = "You Gain " + level.getCoinRewards() + " Coins!";
                            coinReward.setText(string);
                        } else {
                            string = "Player 1 Wins!";
                            winLose.setText(string);
                            coinReward.setText(null);
                        }
                        activity.findViewById(R.id.Result).setVisibility(View.VISIBLE);
                    }
                });
                while (isGameActive.get()) {
                    // busy wait until game ends
                }
                return;
            }

            switch (currentState.get()) {
                case PREPARE:
                    if (isAi && player == Id.Player.TWO) {
                        if (System.gameFlow) java.lang.System.out.println("Ai'turn");
                        // summon units
                        int count = 0;
                        int num = level.getUnitNum();
                        while (rearBattleField.get(player).getAvailableTileNum() > 0 && count < num) {
                            int randomPos = random.nextInt(CARD_NUM);
                            placeUnit(randomPos);
                            generateUnitCard(randomPos);
                            count++;
                            if (System.gameFlow) java.lang.System.out.println("summon enemy");
                        }

                        // use item
                        count = 0;
                        num = level.getItemNum();
                        while (rearBattleField.get(player).getAvailableTileNum() > 0 && count < num) {
                            int randomPos = random.nextInt(CARD_NUM);
                            useItem(randomPos);
                            generateItemCard(randomPos);
                            count ++;
                            if (System.gameFlow) java.lang.System.out.println("Ai use items");
                        }

                        // fight if all enemies finish moving
                        for (Unit unit : unitInCombat.get(player).getCopyOfContent()) {
                            if (unit.getMoveTile() != null) return;
                        }
                        goFight();
                    } else {
                        if (System.gameFlow) java.lang.System.out.println("waiting for input");
                    }
                    break;
                case MOVING:
                    if (currentUnit == null) {
                        if (System.gameFlow) java.lang.System.out.println("select unit");
                        currentUnit = selectNextUnit();
                        if (currentUnit == null) {
                            if (System.gameFlow)
                                java.lang.System.out.println("no unit is ready, switch turn");
                            switchPlayer();
                            screenFocusOn(castle.get(player));
                            postCost();
                            currentState.set(Id.GameState.PREPARE);

                            // reset isReady for units
                            for (Unit unit : unitInCombat.get(player).getCopyOfContent()) {
                                unit.setReady(true);
                            }
                            // take turns
                            return;
                        } else {
                            if (System.gameFlow)
                                java.lang.System.out.println("selected, decide strategy");
                            screenFocusOn(currentUnit);
                            if (currentUnit.getCurrentTile() == null) {
                                currentUnit = null;
                                return;
                            } else {
                                currentUnit.decideStrategy(terrain);
                            }
                        }
                    }
                    if (currentUnit.getMoveTile() == null) {
                        if (System.gameFlow) java.lang.System.out.println("finish moving");
                        // go to combat
                        if (currentUnit.getActionTile() != null) {
                            if (System.gameFlow) java.lang.System.out.println("have action tile");
                            attacker = currentUnit;
                            defender = currentUnit.getActionTile().getUnit();
                            attacker.setOpponent(defender);
                            defender.setOpponent(attacker);
                            attacker.setCombatView(attackerView);
                            defender.setCombatView(defenderView);
                            attacker.changeDirection();
                            defender.changeDirection();
                            // targeting
                            if (!target.isVisible()) {
                                if (System.gameFlow)
                                    java.lang.System.out.println("start targeting");
                                target.setX(currentUnit.getSprite().getX());
                                target.setVisible(true);
                                target.setMoveTile(currentUnit.getActionTile());
                            } else {
                                if (System.gameFlow) java.lang.System.out.println("targeting");
                                if (target.getMoveTile() == null) {
                                    if (System.gameFlow)
                                        java.lang.System.out.println("finish targeting, switch to combat");
                                    // before go to combat
                                    final Drawable drawable = System.getRandomCombatBackground(System.getScreenWidth() * 3 / 4);
                                    System.runOnUi(new Runnable() {
                                        @Override
                                        public void run() {
                                            combatBoard.setBackground(drawable);
                                        }
                                    });
                                    target.setVisible(false);
                                    combat = new Combat(attacker, defender);
                                    currentState.set(Id.GameState.COMBAT);
                                }
                            }
                        } else {
                            if (System.gameFlow)
                                java.lang.System.out.println("no action tile, end this character's turn");
                            currentUnit.setReady(false);
                            currentUnit = null;
                        }
                    } else {
                        if (System.gameFlow) java.lang.System.out.println("moving");
                        screenFocusOn(currentUnit);
                    }
                    break;
                case COMBAT:
                    // if combat finished
                    if (combat == null) {
                        if (System.gameFlow)
                            java.lang.System.out.println("combat finished, end this character's turn");
                        currentUnit.setReady(false);
                        currentUnit = null;
                        currentState.set(Id.GameState.MOVING);
                    } else {
                        if (System.gameFlow) java.lang.System.out.println("start combat");
                        // cause delay between turns
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // fight and see if combat is done
                        if (combat.fight()) {
                            attacker.setCombatView(null);
                            defender.setCombatView(null);
                            combat = null;
                        }
                    }
            }
        }

        @Override
        public void run() {
            while (isGameActive.get()) {
                reaSleepTime = SystemClock.uptimeMillis() + LOGIC_SLEEP_TIME.get();
                gameLogic();
                reaSleepTime -= SystemClock.uptimeMillis();
                if (reaSleepTime > 0) {
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
    private class GameConstant implements Runnable {
        public void update() {
            for (Unit unit : unitInCombat.get(Id.Player.ONE).getCopyOfContent()) {
                unit.animate();
            }
            for (Unit unit : unitInCombat.get(Id.Player.TWO).getCopyOfContent()) {
                unit.animate();
            }
        }

        @Override
        public void run() {
            while (isGameActive.get()) {
                update();
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
    private class GameScreen implements Runnable {
        private long realSleepTime;

        private void gameScreen() {
            screen = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(screen);
            canvas.drawBitmap(background, 0, backgroundY, paint);
            terrain.draw(canvas, paint);
            castle.get(Id.Player.ONE).draw(canvas, paint);
            castle.get(Id.Player.TWO).draw(canvas, paint);

            // draw lines to distinguish sections
            if (hasLine.get()) {
                for (Terrain.BattleField battleField : terrain.getBattleFields()) {
                    if (battleField.getId() == 0) continue;
                    int x = battleField.getTiles()[0].getX();
                    canvas.drawLine(x, 0, x, System.getScreenHeight(), paint);
                }
            }

            if (target.isVisible()) {
                target.draw(canvas, paint);
            }
            for (Unit unit : unitInCombat.get(Id.Player.ONE).getCopyOfContent()) {
                unit.draw(canvas, paint);
            }
            for (Unit unit : unitInCombat.get(Id.Player.TWO).getCopyOfContent()) {
                unit.draw(canvas, paint);
            }

            System.runOnUi(new Runnable() {
                @Override
                public void run() {
                    //if (currentState.get() != Id.GameState.COMBAT)
                    gameScreenView.setImageBitmap(screen);
                    // set visibility
                    if (currentState.get() == Id.GameState.PREPARE) {
                        if (unitMenu.getVisibility() == View.GONE) {
                            if (player == Id.Player.ONE || !isAi) {
                                unitMenu.setVisibility(View.VISIBLE);
                            }
                        }
                        if (combatBoard.getVisibility() == View.VISIBLE)
                            combatBoard.setVisibility(View.GONE);
                    } else if (currentState.get() == Id.GameState.COMBAT) {
                        if (unitMenu.getVisibility() == View.VISIBLE)
                            unitMenu.setVisibility(View.GONE);
                        if (combatBoard.getVisibility() == View.GONE)
                            combatBoard.setVisibility(View.VISIBLE);
                    } else if (currentState.get() == Id.GameState.MOVING) {
                        if (unitMenu.getVisibility() == View.VISIBLE)
                            unitMenu.setVisibility(View.GONE);
                        if (combatBoard.getVisibility() == View.VISIBLE)
                            combatBoard.setVisibility(View.GONE);
                    }

                    if (System.gameFps)
                        java.lang.System.out.println("after ui set before wake: " + SystemClock.uptimeMillis());

                    // tell screen thread ui is updated
                    synchronized (screenCondition) {
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
                gameScreen();
                if (System.gameFps)
                    java.lang.System.out.println("before wait after ui: " + SystemClock.uptimeMillis());
                // wait until ui thread finish its work
                try {
                    synchronized (screenCondition) {
                        while (!screenCondition.finished) screenCondition.wait();
                        screenCondition.finished = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isGameActive.get()) return;
                realSleepTime -= SystemClock.uptimeMillis();
                if (System.gameFps)
                    java.lang.System.out.println("real sleep time: " + realSleepTime);
                if (realSleepTime > 0) {
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

                if (System.gameFps) java.lang.System.out.println("FPS: " + framePerSecond);
            }
        }
    }
}
