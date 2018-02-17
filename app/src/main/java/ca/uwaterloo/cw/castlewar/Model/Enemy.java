package ca.uwaterloo.cw.castlewar.Model;


/**
 * Created by harri on 2018/2/14.
 */

public class Enemy extends Units {

    public Enemy(int id, String name, int hp, int maxHp, int attack, int defense, int speed, int cost, int move, int range)
    {
        super(id, name, SystemData.TypeId.ENEMY.id(), hp, maxHp, attack, defense, speed, cost, move, range);
    }

    public static class Skeleton extends Enemy
    {
        public Skeleton() {
            super(SystemData.EnemyId.SKELETON.id(), "Skeleton",100, 100, 50, 20, 5, 1, 1, 1);
        }
    }

    public static class Slime extends Enemy
    {
        public Slime() {
            super(SystemData.EnemyId.SLIME.id(), "Slime",30, 30, 80, 0, 0, 2, 1, 2);
        }
    }

    public static class Zombie extends  Enemy
    {
        public Zombie() {
            super(SystemData.EnemyId.ZOMBIE.id(), "Zombie",50, 50, 30, 10, 10, 1, 1, 3);
        }
    }

}
