package ca.uwaterloo.cw.castlewar.Model;

import java.util.HashMap;

/**
 * Created by harrison33 on 2018/2/3.
 */

public class UserProfile {

    private static Id currentLevel;
    private static int maxCost;
    private static final Coin COIN = new Coin();
    private static final HashMap<Ally, Boolean> hasAllies = new HashMap<>(Id.getAllyNum());
    private static final HashMap<Ally, Boolean> hasEnemies  = new HashMap<>(Id.getEnemyNum());
    private static final HashMap<Ally, Boolean> hasCastles  = new HashMap<>(Id.getCastleNum());
    private static final HashMap<Ally, Integer> potionsNum = new HashMap<>(Id.getPotionNum());

    public static void readFromDatabase()
    {
        currentLevel = Id.ONE_SIX;
        maxCost = 5;
        for (int i = Id.ALLY_START.ordinal() + 1; i < Id.ALLY_END.ordinal(); ++i)
        {
            hasAllies.put(Id.values()[i], true);
        }
        for (int i = Id.ENEMY_START.ordinal() + 1; i < Id.ENEMY_END.ordinal(); ++i)
        {
            hasEnemies.put(Id.values()[i], true);
        }
        for (int i = Id.POTION_START.ordinal() + 1; i < Id.POTION_END.ordinal(); ++i)
        {
            hasCastles.put(Id.values()[i], true);
        }
        for (int i = Id.CASTLE_START.ordinal() + 1; i < Id.CASTLE_END.ordinal(); ++i)
        {
            potionsNum.put(Id.values()[i], 5);
        }
    }

    public static int getMaxCost() {
        return maxCost;
    }

    public static int getAvailableLevelNum()
    {
        return currentLevel.ordinal() - Id.LEVEL_START.ordinal();
    }

    public static Coin getCOIN() {
        return COIN;
    }
}
