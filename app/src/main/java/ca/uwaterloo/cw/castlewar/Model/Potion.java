package ca.uwaterloo.cw.castlewar.Model;



/**
 * Created by harri on 2018/2/14.
 */

public class Potion extends Item {

    private int hpRecover;
    private Buff buff;
    private int cost;

    public Potion(int id, String name, int buyPrice, int sellPrice, int hpRecover, Buff buff, int cost)
    {
        super(id, name, SystemData.TypeId.POTION.id(), buyPrice, sellPrice);
        this.hpRecover = hpRecover;
        this.buff = buff;
        this.cost = cost;
    }

    public static class HpPotion extends Potion
    {
        public HpPotion()
        {
            super(SystemData.PotionId.HP.id(), "Health Potion", 100, 50, 50, null, 1);
        }
    }

    public static class AttackPotion extends Potion
    {
        public AttackPotion()
        {
            super(SystemData.PotionId.ATTACK.id(), "Attack Potion", 300, 200, 0, null, 1);
        }
    }

    public static class DefensePotion extends Potion
    {
        public DefensePotion()
        {
            super(SystemData.PotionId.DEFENSE.id(), "Defense Potion", 200, 100, 0, null, 1);
        }
    }

    public static class SpeedPotion extends Potion
    {
        public SpeedPotion()
        {
            super(SystemData.PotionId.SPEED.id(), "Speed Potion", 100, 50, 0, null, 1);
        }
    }
}
