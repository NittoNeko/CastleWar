package ca.uwaterloo.cw.castlewar.Structure;

/**
 * Created by harri on 2018/2/24.
 */

abstract public class Id {
    public enum Lawful {
        // Lawful Units
        SWORDMAN, ARCHER, MAGE,

    }

    public enum Chaotic {
            // Chaotic Units
        BANDIT,THIEF,RANGER,
    }

    public enum Castle {
        // Castle Units
        HOLY_CASTLE, EVIL_CASTLE,
    }

    public enum Item {
        // Potion item
        HP_POTION, ATTACK_POTION, DEFENSE_POTION, SPEED_POTION,
    }

    public enum Buff {
        ATTACK, DEFENSE, SPEED,
    }

    public enum Level {
        ONE_ONE, ONE_TWO, ONE_THREE, ONE_FOUR, ONE_FIVE, ONE_SIX,
    }

    public enum Terrain {
        FOREST,
    }

    public enum GameState {
        PREPARE, MOVING, COMBAT
    }

    public enum Thread {
        DATA, SCREEN, ANIME
    }

    public enum CombatRole {
        ATTACKER, DEFENDER;
    }

    public enum RoundRole {
        ACTIVE, PASSIVE
    }

    public enum CombatStage {
        INITIAL, START, FIGHT, END, FINAL
    }

    public enum Direction {
        LEFT, RIGHT
    }

    public enum Player {
        ONE, TWO;

        public Id.Player getOpponent() {
            if (this == ONE) {
                return TWO;
            } else {
                return ONE;
            }
        }
    }

    public enum Image {
        PORTRAIT, IDLE, WALK, RUN, ATTACK, DIE
    }
}
