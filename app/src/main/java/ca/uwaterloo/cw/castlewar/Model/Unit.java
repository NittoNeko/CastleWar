package ca.uwaterloo.cw.castlewar.Model;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Sampler;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ca.uwaterloo.cw.castlewar.Activity.MultithreadGameLogic;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Unit extends GameObject{
    public final static int ROW = 4;
    public final static int COLUMN = 3;
    public final AtomicInteger hp = new AtomicInteger();
    public final AtomicInteger maxHp = new AtomicInteger();
    public final AtomicInteger attack = new AtomicInteger();
    public final AtomicInteger defense = new AtomicInteger();
    public final AtomicInteger speed = new AtomicInteger();
    public int move;
    private int minRange;
    private int maxRange;
    private boolean isIndexLeft;
    private boolean isAttacker;
    private int currentIndex;
    private AtomicBoolean isLeft = new AtomicBoolean();
    public final AtomicInteger cost = new AtomicInteger();
    private boolean isPlayer1;
    private boolean isReady;
    private Terrain.Tile currentTile;
    private Terrain.Tile moveTile;
    private Terrain.Tile actionTile;
    private ArrayList<Bitmap> rightMovingImage;
    private ArrayList<Bitmap> leftMovingImage;
    private int movingImageNum;
    private ArrayList<Buff> currentBuffs = new ArrayList<>();

    public Unit(int id, String name, int resource, int hp, int maxHp, int attack, int defense, int speed, int move, int minRange, int maxRange, int cost) {
        super(id, name, resource);
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.hp.set(hp);
        this.maxHp.set(maxHp);
        this.attack.set(attack);
        this.defense.set(defense);
        this.speed.set(speed);
        this.move = move;
        this.cost.set(cost);
        this.movingImageNum = 3;
        this.rightMovingImage = new ArrayList<>(movingImageNum);
        this.leftMovingImage = new ArrayList<>(movingImageNum);
        this.isPlayer1 = true;
        this.isLeft.set(!isPlayer1);
        this.currentIndex = 0;
        this.isIndexLeft = true;
        this.moveTile = null;
        this.actionTile = null;
        this.isAttacker = true;
        this.isReady = true;
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        int width = original.getWidth() / COLUMN;
        int height = original.getHeight() / ROW;
        setPortrait(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 1 * width,0,width,height)));
        original.recycle();
    }

    protected void createMovingImage() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        int width = original.getWidth() / COLUMN;
        int height = original.getHeight() / ROW;
        addLeftMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 0 * width,1 * height,width,height)));
        addLeftMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 1 * width,1 * height,width,height)));
        addLeftMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 2 * width,1 * height,width,height)));
        addRightMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 0 * width,2 * height,width,height)));
        addRightMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 1 * width,2 * height,width,height)));
        addRightMovingImage(SystemData.scaleIconBitmap(Bitmap.createBitmap(original, 2 * width,2 * height,width,height)));
        original.recycle();
    }

    public Bitmap getMovingImage()
    {
        if (rightMovingImage.isEmpty())
            createMovingImage();
        else if (leftMovingImage.isEmpty())
            createMovingImage();

        if (isLeft.get())
            return leftMovingImage.get(currentIndex);
        else
            return rightMovingImage.get(currentIndex);
    }

    protected void addRightMovingImage(Bitmap image)
    {
        rightMovingImage.add(image);
        setY(SystemData.getGroundLine() - image.getHeight());
    }


    protected void addLeftMovingImage(Bitmap image)
    {
        leftMovingImage.add(image);
        setY(SystemData.getGroundLine() - image.getHeight());
    }

    public void clearStrategy()
    {
        actionTile = null;
        moveTile = null;
    }

    private boolean checkHasEnemy(Terrain.Tile start, Terrain.Tile end, Terrain terrain){
        boolean isIncrease = start.getX() < end.getX();
        for (int i = start.getParentId(); (isIncrease ? i <= end.getParentId() : i >= end.getParentId());){
            if (i == start.getParentId()){
                Terrain.BattleField battleField = start.getParent();
                for (int j = start.getId(); isIncrease ? j < battleField.getTiles().length : j >= 0; ){
                    if (!battleField.getTiles()[j].isAvailable()){
                        if (battleField.getTiles()[j].getUnit().isPlayer1() != isPlayer1) return true;
                    }
                    if (isIncrease) j++;
                    else j--;
                }
            } else if (i == end.getParentId()){
                Terrain.BattleField battleField = end.getParent();
                for (int j = end.getId(); !isIncrease ? j < battleField.getTiles().length : j >= 0; ){
                    if (!battleField.getTiles()[j].isAvailable()){
                        if (battleField.getTiles()[j].getUnit().isPlayer1() != isPlayer1) return true;
                    }
                    if (!isIncrease) j++;
                    else j--;
                }
            } else{
                Terrain.BattleField battleField = terrain.getBattleFields()[i];
                for (int j = 0; j < battleField.getTiles().length; ++j ) {
                    if (!battleField.getTiles()[j].isAvailable()) {
                        if (battleField.getTiles()[j].getUnit().isPlayer1() != isPlayer1)
                            return true;
                    }
                }
            }
            if (isIncrease) i++;
            else i--;
        }
        return false;
    }

    // common way to take actions
    // go as near as possible to the first enemy who is the nearest to ally castle
    // if close enough, set aim to that
    // if not, attack nearby enemies
    public void decideStrategy(Terrain terrain) {

        boolean isAimleft = false;
        actionTile = null;
        moveTile = null;

        // find first enemy
        outerloop1:
        for (Terrain.BattleField battleField : this.isPlayer1 ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Terrain.Tile tile : this.isPlayer1 ? battleField.getTiles() : battleField.getReversedTiles()){
                if (!tile.isAvailable()){
                    if (tile.getUnit().isPlayer1() != isPlayer1){
                        actionTile = tile;
                        break outerloop1;
                    }
                }
            }
        }

        // if no such target, just move forward
        if (actionTile == null) {
            for (Terrain.BattleField battleField : !this.isPlayer1 ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
                for (Terrain.Tile tile : this.isPlayer1 ? battleField.getTiles() : battleField.getReversedTiles()){
                    if (tile.isAvailable()
                            && Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= this.move
                            && !checkHasEnemy(this.currentTile, tile, terrain)) {
                        moveTile = tile;
                        return;
                    }
                }
            }
        }
        // if exist, get its direction
        else {
            if (actionTile.getParentId() < this.getCurrentTile().getParentId()){
                isAimleft = true;
            }
            else if (actionTile.getParentId() > this.getCurrentTile().getParentId()){
                isAimleft = false;
            } else{
                if (isPlayer1) isAimleft = true;
                else{
                    isAimleft = false;
                }
            }
        }

        // find first movable and attackable tile near enemy castle
        for (Terrain.BattleField battleField : !this.isPlayer1 ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Terrain.Tile tile : this.isPlayer1 ? battleField.getTiles() : battleField.getReversedTiles()){
                if (tile.isAvailable()){
                    if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= this.move
                            && Math.abs(actionTile.getParentId() - tile.getParentId()) <= this.maxRange
                            && Math.abs(actionTile.getParentId() - tile.getParentId()) >= this.minRange
                            && !checkHasEnemy(this.currentTile, tile, terrain)){
                        moveTile = tile;
                        if (moveTile.getParentId() == this.getCurrentTile().getParentId()) moveTile = null;
                        return;
                    }
                }
            }
        }

        // if no such tile, just move toward according to the direction
        if (moveTile == null){
            actionTile = null;
            outerloop3:
            for (Terrain.BattleField battleField : isAimleft ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
                for (Terrain.Tile tile : !isAimleft ? battleField.getTiles() : battleField.getReversedTiles()){
                    if (tile.isAvailable()){
                        if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= this.move
                                && (isAimleft ? tile.getParentId() <= this.getCurrentTile().getParentId() : tile.getParentId() >= this.getCurrentTile().getParentId())
                                && !checkHasEnemy(this.currentTile, tile, terrain)){
                            moveTile = tile;
                            break outerloop3;
                        }
                    }
                }
            }
        }

        // if still no such tile the unit can move to, stay and attack nearby enemy
        // no matter if action tile exists, find nearby enemy
        for (Terrain.BattleField battleField : this.isPlayer1 ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Terrain.Tile tile : this.isPlayer1 ? battleField.getTiles() : battleField.getReversedTiles()) {
                if (!tile.isAvailable()){
                    if (tile.getUnit().isPlayer1() != this.isPlayer1){
                        if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= this.maxRange
                                && Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) >= this.minRange){
                            actionTile = tile;
                            return;
                        }
                    }
                }
            }
        }
    }

    public int attack(Unit defender){
        // prepare attack
        int damage = this.attack.get() - defender.defense.get();
        if (damage < 0) damage = 0;
        return damage;
    }

    // take in attacker and damage as input
    // return current hp
    public int takeDamage(Unit attacker, int damage){
        int currentHp = this.hp.get() - damage;
        if (currentHp < 0) currentHp = 0;
        return currentHp;
    }

    public boolean isDead(){
        if (hp.get() <= 0) {
            this.getCurrentTile().setUnit(null);
            return true;
        }
        else return false;
    }

    public void animate()
    {
        if (movingImageNum <= 1) return;
        if (currentIndex == 0)
            isIndexLeft = false;
        else if (currentIndex == movingImageNum - 1)
            isIndexLeft = true;

        if (isIndexLeft)
            currentIndex--;
        else
            currentIndex++;
    }

    public void changeDirection(Terrain.Tile tile){
        if (tile.getX() > this.currentTile.getX()){
            isLeft.set(false);
        } else {
            isLeft.set(true);
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setLeft(boolean isLeft) {
        this.isLeft.set(isLeft);
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public void setPlayer1(boolean player1) {
        isPlayer1 = player1;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public boolean isAttacker() {
        return isAttacker;
    }

    public void setAttacker(boolean attacker) {
        isAttacker = attacker;
    }

    public Terrain.Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Terrain.Tile currentTile) {
        this.currentTile = currentTile;
    }

    public Terrain.Tile getMoveTile() {
        return moveTile;
    }

    public Terrain.Tile getActionTile() {
        return actionTile;
    }

    public ArrayList<Buff> getCurrentBuffs() {
        return currentBuffs;
    }
}
