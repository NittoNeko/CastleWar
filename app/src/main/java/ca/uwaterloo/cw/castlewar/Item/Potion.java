package ca.uwaterloo.cw.castlewar.Item;


import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Potion extends Item {

    public Potion(int id, String name,String description, int resource, int basePrice, Status status) {
        super(id, name, description, resource, basePrice, status);
    }

    public static ArrayList<Item> getAllPotion() {
        ArrayList<Item> potion = new ArrayList<>();
        potion.add(new HpPotion());
        potion.add(new AttackPotion());
        potion.add(new DefensePotion());
        potion.add(new SpeedPotion());
        return potion;
    }

    @Override
    public void use() {

    }

    public static class HpPotion extends Potion
    {
        public HpPotion() {
            super(Id.Item.HP_POTION.ordinal(), "Health Potion",
                    "The HP Portion costs 100 coins to buy, and gives 50 coins after selling, it can heal and increase your HP to maximum.",
                    R.drawable.potion_hp, 100, new Status(1));
        }

    }

    public static class AttackPotion extends Potion
    {
        public AttackPotion()
        {
            super(Id.Item.ATTACK_POTION.ordinal(), "Attack Potion",
                    "The Attack Potion costs 300 coins to buy, and gives 200 coins after selling, it can increase your character's attack ability.",
                    R.drawable.potion_attack, 300, new Status(1));
        }
    }

    public static class DefensePotion extends Potion
    {
        public DefensePotion() {
            super(Id.Item.DEFENSE_POTION.ordinal(), "Defense Potion",
                    "The Defense Potion costs 200 coins to buy, and give 100 coins after selling, it can increase your character's defense ability.",
                    R.drawable.potion_defense, 200, new Status(1));
        }
    }

    public static class SpeedPotion extends Potion
    {
        public SpeedPotion() {
            super(Id.Item.SPEED_POTION.ordinal(), "Speed Potion",
                    "The Speed Potion costs 100 coins to buy, and give 50 coins after selling, it can increase your character's speed." ,
                    R.drawable.potion_speed, 100, new Status(1));
        }
    }
}
