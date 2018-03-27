package ca.uwaterloo.cw.castlewar.Effect;

import ca.uwaterloo.cw.castlewar.Base.GameObject;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Unit.Unit;

/**
 * Created by harri on 2018/3/24.
 */

abstract public class Effect extends GameObject {
    private Id.CombatStage triggerStage = Id.CombatStage.START;
    private Unit unit;
    private int turnLeft;

    public Effect(int id, String name,String description, int resource) {
        super(id, name,description, resource);
    }

    public int getTurnLeft() {
        return turnLeft;
    }

    public void reduceTurn() {
        this.turnLeft--;
    }

    // called for actual side effects like poison
    public void apply(Unit unit) {
        // no op
    }

    // called for status changes like brave
    public void reapply(Unit unit) {
        // no op
    }

    public void overApply(Effect effect) {
        // simply extend the time
        this.turnLeft += effect.turnLeft;
    }

    public Id.CombatStage getTriggerStage() {
        return triggerStage;
    }
}
