package ca.uwaterloo.cw.castlewar.Model;


/**
 * Created by harri on 2018/2/14.
 */

public class Tower extends Units {

    private static final int SPEED = 0;
    private static final int MOVE = 0;
    private static final int RANGE = 0;
    private static final int COST = 0;

    public Tower(int id, String name, int hp, int maxHp, int attack, int defense)
    {
        super(id, name, SystemData.TypeId.TOWER.id(), hp, maxHp, attack, defense, SPEED, MOVE, RANGE, COST);
    }
}
