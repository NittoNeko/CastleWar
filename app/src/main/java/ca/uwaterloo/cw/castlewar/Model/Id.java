package ca.uwaterloo.cw.castlewar.Model;

/**
 * Created by harri on 2018/2/24.
 */

public class Id {
    public enum Unit
    {
        // Ally Units
        SWORDMAN, ARCHER, MAGE,


        // Enemy Units
        SKELETON, ZOMBIE, SLIME,
    }

    public enum Castle
    {
        HOLY, EVIL,
    }

    public enum Item
    {
        // Potion item
        HP_POTION, ATTACK_POTION, DEFENSE_POTION, SPEED_POTION,
    }

    public enum Buff
    {
        ATTACK, DEFENSE, SPEED,
    }

    public enum Level
    {
        ONE_ONE, ONE_TWO, ONE_THREE, ONE_FOUR, ONE_FIVE, ONE_SIX,
    }
    public enum Terrain
    {
        FOREST,
    }
}
