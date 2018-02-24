package ca.uwaterloo.cw.castlewar.Model;

import java.util.HashMap;

/**
 * Created by harrison33 on 2018/2/3.
 */

public class UserProfile {

    private static SystemData.LevelId currentLevel;
    private static final Coin COIN = new Coin();
    private static final HashMap<SystemData.AllyId, Boolean> hasAllies = new HashMap<>(SystemData.AllyId.values().length);
    private static final HashMap<SystemData.EnemyId, Boolean> hasEnemies  = new HashMap<>(SystemData.EnemyId.values().length);
    private static final HashMap<SystemData.CastleId, Boolean> hasCastles  = new HashMap<>(SystemData.CastleId.values().length);
    private static final HashMap<SystemData.PotionId, Integer> potionsNum = new HashMap<>(SystemData.PotionId.values().length);

    public static void readFromDatabase()
    {
        for (SystemData.AllyId id : SystemData.AllyId.values())
        {
            hasAllies.put(id, true);
        }
        for (SystemData.EnemyId id : SystemData.EnemyId.values())
        {
            hasEnemies.put(id, true);
        }
        for (SystemData.CastleId id : SystemData.CastleId.values())
        {
            hasCastles.put(id, true);
        }
        for (SystemData.PotionId id : SystemData.PotionId.values())
        {
            potionsNum.put(id, 5);
        }
    }
}
