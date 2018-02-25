package ca.uwaterloo.cw.castlewar.Model;

/**
 * Created by harri on 2018/2/24.
 */

public class CombatObject extends GameObject{
    private int cost;

    public CombatObject(Id id, String name, int resource, int cost) {
        super(id, name, resource);
        this.cost = cost;
    }

    public int getCost()
    {
        return cost;
    }
}
