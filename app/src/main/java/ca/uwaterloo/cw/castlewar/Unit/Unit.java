package ca.uwaterloo.cw.castlewar.Unit;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;

import ca.uwaterloo.cw.castlewar.Base.Animation;
import ca.uwaterloo.cw.castlewar.Effect.Buff;
import ca.uwaterloo.cw.castlewar.Effect.Effect;
import ca.uwaterloo.cw.castlewar.Base.GameObject;
import ca.uwaterloo.cw.castlewar.Game.Combat;
import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Structure.CombatView;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Base.Sprite;
import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Game.Terrain;
import ca.uwaterloo.cw.castlewar.Base.Tile;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Unit extends GameObject {
    protected static final int MOVE_SPEED = 30;
    private Id.Player player;
    private boolean isReady;
    private Tile currentTile;
    private Tile moveTile;
    private Tile actionTile;
    private Id.RoundRole roundRole;
    private Unit opponent;
    private int level;
    private Damage damage;
    private ArrayList<Effect> currentEffects = new ArrayList<>();
    private Ability attackAbility;
    private Ability defenseAbility;
    private Ability utilityAbility;
    private CombatView combatView;


    public Unit(int id, String name, String description, int resource, Status status, ArrayList<Integer> idle, ArrayList<Integer> walk, ArrayList<Integer> run, ArrayList<Integer> attack, ArrayList<Integer> die) {
        super(id, name, description, resource, status);
        this.player = Id.Player.ONE;
        this.moveTile = null;
        this.actionTile = null;
        this.isReady = true;
        this.level = 1;
        this.getSprite().setConfig(Tile.SIZE, Tile.SIZE, 4);
        this.getSprite().addResources(Id.Image.IDLE, idle);
        this.getSprite().addResources(Id.Image.WALK, walk);
        this.getSprite().addResources(Id.Image.RUN, run);
        this.getSprite().addResources(Id.Image.ATTACK, attack);
        this.getSprite().addResources(Id.Image.DIE, die);
        this.getSprite().setY(System.getGroundLine() - Tile.SIZE);
        this.getSprite().switchImage(Id.Image.IDLE);
    }

    public void clone(Unit unit) {
        this.level = unit.level;
        this.getSprite().clone(unit.getSprite());
        this.attackAbility = unit.attackAbility;
        this.defenseAbility = unit.defenseAbility;
        this.utilityAbility = unit.utilityAbility;
    }

    private boolean checkHasEnemy(Tile start, Tile end, Terrain terrain){
        boolean isIncrease = start.getX() < end.getX();
        for (int i = start.getParentId(); (isIncrease ? i <= end.getParentId() : i >= end.getParentId());){
            if (i == start.getParentId()){
                Terrain.BattleField battleField = start.getParent();
                for (int j = start.getId(); isIncrease ? j < battleField.getTiles().length : j >= 0; ){
                    if (!battleField.getTiles()[j].isAvailable()){
                        if (battleField.getTiles()[j].getUnit().getPlayer() != player) return true;
                    }
                    if (isIncrease) j++;
                    else j--;
                }
            } else if (i == end.getParentId()){
                Terrain.BattleField battleField = end.getParent();
                for (int j = end.getId(); !isIncrease ? j < battleField.getTiles().length : j >= 0; ){
                    if (!battleField.getTiles()[j].isAvailable()){
                        if (battleField.getTiles()[j].getUnit().getPlayer() != player) return true;
                    }
                    if (!isIncrease) j++;
                    else j--;
                }
            } else{
                Terrain.BattleField battleField = terrain.getBattleFields()[i];
                for (int j = 0; j < battleField.getTiles().length; ++j ) {
                    if (!battleField.getTiles()[j].isAvailable()) {
                        if (battleField.getTiles()[j].getUnit().getPlayer() != player)
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
        Status status = this.getModifiedStatus();
        Id.Direction aimDirection = this.player == Id.Player.ONE ? Id.Direction.RIGHT : Id.Direction.LEFT;
        actionTile = null;
        moveTile = null;

        // find first enemy
        outerloop1:
        for (Terrain.BattleField battleField : this.player == Id.Player.ONE ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Tile tile : this.player == Id.Player.ONE ? battleField.getTiles() : battleField.getReversedTiles()){
                if (!tile.isAvailable()){
                    if (tile.getUnit().getPlayer() != player){
                        actionTile = tile;
                        if (actionTile.getUnit() instanceof Castle) actionTile = actionTile.getUnit().getCurrentTile();
                        break outerloop1;
                    }
                }
            }
        }

        // if no such target, just move forward
        if (actionTile == null) {
            for (Terrain.BattleField battleField : this.player == Id.Player.TWO ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
                for (Tile tile : this.player == Id.Player.ONE ? battleField.getTiles() : battleField.getReversedTiles()){
                    if (tile.isAvailable()
                            && Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= status.getMove()
                            && !checkHasEnemy(this.currentTile, tile, terrain)) {
                        moveTile = tile;
                        if (moveTile.getParentId() == this.getCurrentTile().getParentId()) moveTile = null;
                        return;
                    }
                }
            }
        } else {
            if (actionTile.getParentId() < this.getCurrentTile().getParentId()){
                aimDirection = Id.Direction.LEFT;
            } else if (actionTile.getParentId() > this.getCurrentTile().getParentId()){
                aimDirection = Id.Direction.RIGHT;
            } else{
                if (player == Id.Player.ONE) aimDirection = Id.Direction.RIGHT;
                else{
                    aimDirection = Id.Direction.LEFT;
                }
            }
        }

        // find first movable and attackable tile near enemy castle
        for (Terrain.BattleField battleField : this.player == Id.Player.TWO ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Tile tile : this.player == Id.Player.ONE ? battleField.getTiles() : battleField.getReversedTiles()){
                if (tile.isAvailable()){
                    if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= status.getMove()
                            && Math.abs(actionTile.getParentId() - tile.getParentId()) <= status.getMaxRange()
                            && Math.abs(actionTile.getParentId() - tile.getParentId()) >= status.getMinRange()
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
            for (Terrain.BattleField battleField : aimDirection == Id.Direction.LEFT ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
                for (Tile tile : aimDirection == Id.Direction.RIGHT ? battleField.getTiles() : battleField.getReversedTiles()){
                    if (tile.isAvailable()){
                        if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= status.getMove()
                                && (aimDirection == Id.Direction.LEFT ? tile.getParentId() <= this.getCurrentTile().getParentId() : tile.getParentId() >= this.getCurrentTile().getParentId())
                                && !checkHasEnemy(this.currentTile, tile, terrain)){
                            moveTile = tile;
                            if (moveTile.getParentId() == this.getCurrentTile().getParentId()) moveTile = null;
                            break outerloop3;
                        }
                    }
                }
            }
        }

        // if still no such tile the unit can move to, stay and attack nearby enemy
        // no matter if action tile exists, find nearby enemy
        for (Terrain.BattleField battleField : this.player == Id.Player.ONE ? terrain.getBattleFields() : terrain.getReversedBattlefield()){
            for (Tile tile : this.player == Id.Player.ONE ? battleField.getTiles() : battleField.getReversedTiles()) {
                if (!tile.isAvailable()){
                    if (tile.getUnit().getPlayer() != this.player){
                        if (Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) <= status.getMaxRange()
                                && Math.abs(tile.getParentId() - this.getCurrentTile().getParentId()) >= status.getMinRange()){
                            actionTile = tile;
                            if (actionTile.getUnit() instanceof  Castle) actionTile = actionTile.getUnit().getCurrentTile();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void startTurn() {
        this.damage = null;
        if (utilityAbility != null && utilityAbility.getTriggerStage() == Id.CombatStage.END) {
            utilityAbility.apply(this);
        }
        applyEffect(Id.CombatStage.START);
    }

    public void attack(){
        // prepare attack
        this.damage = new Damage();
        this.damage.modifyAttack(this.getModifiedStatus().getAttack());

        if (attackAbility != null && attackAbility.getTriggerStage() == Id.CombatStage.FIGHT) {
            attackAbility.apply(this);
        }

        // play attack animation
        final Animation animation = new Animation();
        final ArrayList<Bitmap> attack = this.getSprite().getBitmaps(Id.Image.ATTACK, checkDirection());
    }

    public void defend() {
        // add self defense to the calculation
        this.opponent.getDamage().modifyDefense(this.getModifiedStatus().getDefense());

        if (defenseAbility != null && defenseAbility.getTriggerStage() == Id.CombatStage.FIGHT) {
            defenseAbility.apply(this);
        }

        // play hurt animation

    }

    // take damage
    public void takeDamage(){
        int damage = this.opponent.getDamage().calculate();
        modifyBaseHealth(-damage);
    }

    public void endTurn() {
        this.damage = null;
        if (utilityAbility != null && utilityAbility.getTriggerStage() == Id.CombatStage.END) {
            utilityAbility.apply(this);
        }
        applyEffect(Id.CombatStage.END);
    }

    // here since permanently lose or gain health
    // so modify base health
    public void modifyBaseHealth(int offset) {
        int before = this.getModifiedStatus().getHp();
        int currentHp = this.getBaseStatus().getHp() + offset;
        if (currentHp < 0) currentHp = 0;
        getBaseStatus().setHp(currentHp);
        reapplyEffect();
        int after = this.getModifiedStatus().getHp();
        final Animation animation = new Animation();
        final CombatView combatView = this.combatView;
        animation.setIntValues(before, after);
        animation.setDuration(1000);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int hp = (int) valueAnimator.getAnimatedValue();
                combatView.getHp().setText(Integer.toString(hp));
                combatView.getHealthBar().setProgress(hp);
            }
        });
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });
    }

    public boolean isDead(){
        if (getModifiedStatus().getHp() <= 0) {
            this.getCurrentTile().setUnit(null);
            return true;
        }
        else return false;
    }

    public void receiveEffect(Effect effect) {
        for (Effect currentEffect : this.currentEffects) {
            if (effect.getClass().isInstance(currentEffect) &&
                    effect.getId() == currentEffect.getId()) {
                currentEffect.overApply(effect);
                reapplyEffect();
                return;
            }
        }

        // if not found
        this.currentEffects.add(effect);
        reflectOnView();
        reapplyEffect();
    }

    public void checkEffect() {
        Iterator<Effect> iterator = this.currentEffects.iterator();
        while (iterator.hasNext()) {
            Effect effect = iterator.next();
            effect.reduceTurn();
            if (effect.getTurnLeft() <= 0) iterator.remove();
        }
        reflectOnView();
        reapplyEffect();
    }

    public void reapplyEffect() {
        // reset modified status first
        this.setModifiedStatus(new Status(this.getBaseStatus()));
        for (Effect effect : this.currentEffects) {
            effect.reapply(this);
        }
    }

    // take actual effects like poison
    private void applyEffect(Id.CombatStage stage) {
        for (Effect effect : this.currentEffects) {
            if (effect.getTriggerStage() == stage) effect.reapply(this);
        }
        reapplyEffect();
    }

    public Unit setAllAbility(Ability attackAbility, Ability defenseAbility, Ability utilityAbility) {
        setAttackAbility(attackAbility);
        setDefenseAbility(defenseAbility);
        setUtilityAbility(utilityAbility);
        return this;
    }

    public void setAttackAbility(Ability attackAbility) {
        this.attackAbility = attackAbility;
    }

    public void setDefenseAbility(Ability defenseAbility) {
        this.defenseAbility = defenseAbility;
    }

    public void setUtilityAbility(Ability utilityAbility) {
        this.utilityAbility = utilityAbility;
    }

    public void animate() {
        this.getSprite().switchBitmap();
    }

    public void changeDirection(){
        Tile tile = null;
        if (this.moveTile != null) tile = this.moveTile;
        else if (this.actionTile != null) tile = this.actionTile;
        else return;

        if (tile.getX() > this.currentTile.getX()){
            this.getSprite().setDirection(Id.Direction.RIGHT);
        } else {
            this.getSprite().setDirection(Id.Direction.LEFT);
        }
    }

    public void move(){
        Sprite sprite = this.getSprite();
        if (moveTile == null) return;
        if (moveTile.getX() > sprite.getX()){
            // finish move
            if (sprite.getDirection() == Id.Direction.LEFT) sprite.setDirection(Id.Direction.RIGHT);
            if (sprite.getX() + MOVE_SPEED > moveTile.getX()) sprite.setX(moveTile.getX());
            else sprite.setX(sprite.getX() + MOVE_SPEED);
        } else if (moveTile.getX() < sprite.getX()){
            // finish move
            if (sprite.getDirection() == Id.Direction.RIGHT) sprite.setDirection(Id.Direction.LEFT);
            if (sprite.getX() - MOVE_SPEED < moveTile.getX()) sprite.setX(moveTile.getX());
            else sprite.setX(sprite.getX() - MOVE_SPEED);
        } else{
            // if not just initialized
            if (currentTile != null){
                currentTile.setUnit(null);
            }
            // finish move
            currentTile = moveTile;
            currentTile.setUnit(this);
            moveTile = null;
        }
    }

    public void reflectOnView() {
        if (combatView == null) return;
        final Status status = this.getModifiedStatus();
        final Bitmap portrait = this.getSprite().getPortrait(checkDirection());
        final ArrayList<Effect> effects = this.currentEffects;
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                combatView.getHp().setText(Integer.toString(status.getHp()));
                combatView.getMaxHp().setText(Integer.toString(status.getMaxHp()));
                combatView.getUnit().setImageBitmap(portrait);
                combatView.getAnimation().setImageBitmap(null);
                combatView.getHealthBar().setMax(status.getMaxHp());
                combatView.getHealthBar().setProgress(status.getHp());
                combatView.getEffects().removeAllViews();
                for (Effect effect : effects) {
                    ImageView imageView = new ImageView(System.context());
                    imageView.setImageBitmap(effect.getPortrait());
                    combatView.getEffects().addView(imageView);
                }
            }
        });
    }

    private Id.Direction checkDirection() {
        return this.roundRole == Id.RoundRole.ACTIVE ? Id.Direction.RIGHT : Id.Direction.LEFT;
    }

    public Unit getOpponent() {
        return opponent;
    }

    public void setOpponent(Unit opponent) {
        this.opponent = opponent;
    }

    public void setMoveTile(Tile moveTile) {
        this.moveTile = moveTile;
    }

    public void setActionTile(Tile actionTile) {
        this.actionTile = actionTile;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public Id.Player getPlayer() {
        return player;
    }

    public void setPlayer(Id.Player player) {
        this.player = player;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public Tile getMoveTile() {
        return moveTile;
    }

    public Tile getActionTile() {
        return actionTile;
    }

    public ArrayList<Effect> getCurrentEffects() {
        return currentEffects;
    }

    public Id.RoundRole getRoundRole() {
        return roundRole;
    }

    public void setRoundRole(Id.RoundRole roundRole) {
        this.roundRole = roundRole;
    }

    public int getLevel() {
        return level;
    }

    public Unit setLevel(int level) {
        this.level = level;
        return this;
    }

    public CombatView getCombatView() {
        return combatView;
    }

    public void setCombatView(CombatView combatView) {
        this.combatView = combatView;
        reflectOnView();
    }

    public Damage getDamage() {
        return damage;
    }
}