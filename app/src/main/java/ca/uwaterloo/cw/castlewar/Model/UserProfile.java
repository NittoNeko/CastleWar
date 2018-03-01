package ca.uwaterloo.cw.castlewar.Model;

import java.util.HashMap;

/**
 * Created by harrison33 on 2018/2/3.
 */

public class UserProfile {

    private static Id.Level currentLevel;
    private static int maxCost;
    private static final Coin COIN = new Coin();
    private static final HashMap<Id.Unit, Boolean> hasUnits = new HashMap<>(Id.Unit.values().length);
    private static final HashMap<Integer, Integer> itemNum = new HashMap<>(Id.Item.values().length);

    public static void readFromDatabase()
    {
        currentLevel = Id.Level.ONE_SIX;
        maxCost = 5;
        for (Id.Unit id : Id.Unit.values())
        {
            hasUnits.put(id, true);
        }
        for (Id.Item id : Id.Item.values())
        {
            itemNum.put(id.ordinal(), 5);
        }
    }

    public static int getMaxCost() {
        return maxCost;
    }

    public static int getAvailableLevelNum()
    {
        return currentLevel.ordinal() + 1;
    }

    public static void increaseItemNum(int id) {
        int itemQuantity = itemNum.get(id) + 1;
        itemNum.put(id,itemQuantity);
    }

    public static void decreaseItemNum(int id) {
        int itemQuantity = itemNum.get(id) - 1;
        itemNum.put(id,itemQuantity);
    }

    public static int getItemNum(int id) {
        int itemQuantity = itemNum.get(id);
        return itemQuantity;
    }

    public static Coin getCOIN() {
        return COIN;
    }
}
