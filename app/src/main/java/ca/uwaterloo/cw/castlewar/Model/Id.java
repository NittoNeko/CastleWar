package ca.uwaterloo.cw.castlewar.Model;

/**
 * Created by harri on 2018/2/24.
 */

public enum Id {


    // Function id
    FUNCTION_START,

    COIN, TARGET_ICON,

    FUNCTION_END,


    // Ally id
    ALLY_START,

    SWORDMAN, ARCHER, MAGE,

    ALLY_END,


    // Enemy id
    ENEMY_START,

    SKELETON, ZOMBIE, SLIME,

    ENEMY_END,


    // Castle id
    CASTLE_START,

    HOLY_CASTLE, EVIL_CASTLE,

    CASTLE_END,


    // Potion id
    POTION_START,

    HP_POTION, ATTACK_POTION, DEFENSE_POTION, SPEED_POTION,

    POTION_END,


    // Buff id
    BUFF_START,

    ATTACK_BUFF, DEFENSE_BUFF, SPEED_BUFF,

    BUFF_END,


    // Level id
    LEVEL_START,

    ONE_ONE, ONE_TWO, ONE_THREE, ONE_FOUR, ONE_FIVE, ONE_SIX,

    LEVEL_END,


    // Terrain id
    TERRAIN_START,

    FOREST,

    TERRAIN_END,
    ;

    public static int getAllyNum()
    {
        return ALLY_END.ordinal() - ALLY_START.ordinal()  - 1;
    }

    public static int getEnemyNum()
    {
        return ENEMY_END.ordinal() - ENEMY_START.ordinal() - 1;
    }

    public static int getCastleNum()
    {
        return CASTLE_END.ordinal() - CASTLE_START.ordinal() - 1;
    }

    public static int getPotionNum()
    {
        return POTION_END.ordinal() - POTION_START.ordinal() - 1;
    }

    public static int getLevelNum()
    {
        return  LEVEL_END.ordinal() - LEVEL_START.ordinal() - 1;
    }
}
