package ca.uwaterloo.cw.castlewar.Unit;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Chaotic extends Unit {
    public Chaotic(int id, String name, String description, int resource, Status status, ArrayList<Integer> idle, ArrayList<Integer> walk, ArrayList<Integer> run, ArrayList<Integer> attack, ArrayList<Integer> die)
    {
        super(id, name, description, resource,status, idle, walk, run, attack, die);
    }

    public static ArrayList<Unit> getAllChaotic() {
        ArrayList<Unit> chaotic = new ArrayList<>();
        chaotic.add(new Bandit());
        chaotic.add(new Thief());
        chaotic.add(new Ranger());
        return chaotic;
    }

    public static class Bandit extends Chaotic
    {
        public Bandit() {
            super(Id.Chaotic.BANDIT.ordinal(), "Bandit", "dsa", R.drawable.bandit_idle_000,
                    new Status(80, 80, 30, 10, 5, 1, 0, 0, 1),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.bandit_idle_000,
                            R.drawable.bandit_idle_001,
                            R.drawable.bandit_idle_002,
                            R.drawable.bandit_idle_003,
                            R.drawable.bandit_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.bandit_walk_000,
                            R.drawable.bandit_walk_001,
                            R.drawable.bandit_walk_002,
                            R.drawable.bandit_walk_003,
                            R.drawable.bandit_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.bandit_run_000,
                            R.drawable.bandit_run_001,
                            R.drawable.bandit_run_002,
                            R.drawable.bandit_run_003,
                            R.drawable.bandit_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.bandit_attack_000,
                            R.drawable.bandit_attack_002,
                            R.drawable.bandit_attack_004,
                            R.drawable.bandit_attack_006,
                            R.drawable.bandit_attack_008
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.bandit_die_000,
                            R.drawable.bandit_die_002,
                            R.drawable.bandit_die_004,
                            R.drawable.bandit_die_006,
                            R.drawable.bandit_die_009
                    ))
            );
        }
    }

    public static class Thief extends Chaotic
    {
        public Thief() {
            super(Id.Chaotic.THIEF.ordinal(), "Thief", "dsaR",R.drawable.thief_idle_000,
                    new Status(50, 50, 40, 10, 10, 2, 0, 0, 1),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.thief_idle_000,
                            R.drawable.thief_idle_001,
                            R.drawable.thief_idle_002,
                            R.drawable.thief_idle_003,
                            R.drawable.thief_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.thief_walk_000,
                            R.drawable.thief_walk_001,
                            R.drawable.thief_walk_002,
                            R.drawable.thief_walk_003,
                            R.drawable.thief_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.thief_run_000,
                            R.drawable.thief_run_001,
                            R.drawable.thief_run_002,
                            R.drawable.thief_run_003,
                            R.drawable.thief_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.thief_attack_000,
                            R.drawable.thief_attack_002,
                            R.drawable.thief_attack_004,
                            R.drawable.thief_attack_006,
                            R.drawable.thief_attack_008
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.thief_die_000,
                            R.drawable.thief_die_003,
                            R.drawable.thief_die_005,
                            R.drawable.thief_die_007,
                            R.drawable.thief_die_009
                    ))
            );
        }
    }

    public static class Ranger extends Chaotic
    {
        public Ranger() {
            super(Id.Chaotic.RANGER.ordinal(), "Ranger","dsa", R.drawable.ranger_idle_000,
                    new Status(40, 40, 30, 10, 5, 1, 1, 1, 1),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.ranger_idle_000,
                            R.drawable.ranger_idle_001,
                            R.drawable.ranger_idle_002,
                            R.drawable.ranger_idle_003,
                            R.drawable.ranger_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.ranger_walk_000,
                            R.drawable.ranger_walk_001,
                            R.drawable.ranger_walk_002,
                            R.drawable.ranger_walk_003,
                            R.drawable.ranger_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.ranger_run_000,
                            R.drawable.ranger_run_001,
                            R.drawable.ranger_run_002,
                            R.drawable.ranger_run_003,
                            R.drawable.ranger_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.ranger_attack_000,
                            R.drawable.ranger_attack_003,
                            R.drawable.ranger_attack_005,
                            R.drawable.ranger_attack_007,
                            R.drawable.ranger_attack_009
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.ranger_die_000,
                            R.drawable.ranger_die_003,
                            R.drawable.ranger_die_005,
                            R.drawable.ranger_die_007,
                            R.drawable.ranger_die_009
                    ))
            );
        }
    }
}