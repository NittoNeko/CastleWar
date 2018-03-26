
package ca.uwaterloo.cw.castlewar.Unit;
import ca.uwaterloo.cw.castlewar.Base.Status;
import ca.uwaterloo.cw.castlewar.Structure.Id;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/14.
 */

abstract public class Lawful extends Unit {
    public Lawful(int id, String name, String description, int resource, Status status, ArrayList<Integer> idle, ArrayList<Integer> walk, ArrayList<Integer> run, ArrayList<Integer> attack, ArrayList<Integer> die) {
        super(id, name,description, resource,status, idle, walk, run, attack, die);
    }

    public static ArrayList<Unit> getAllLawful() {
        ArrayList<Unit> lawful = new ArrayList<>();
        lawful.add(new Archer());
        lawful.add(new Mage());
        lawful.add(new SwordMan());
        return lawful;
    }

    public static class Archer extends Lawful {
        public Archer() {
            super(Id.Lawful.ARCHER.ordinal(), "Archer", "123", R.drawable.archer_idle_000,
                    new Status(80, 80, 25, 10, 10, 1, 1, 1, 1),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.archer_idle_000,
                            R.drawable.archer_idle_001,
                            R.drawable.archer_idle_002,
                            R.drawable.archer_idle_003,
                            R.drawable.archer_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.archer_walk_000,
                            R.drawable.archer_walk_001,
                            R.drawable.archer_walk_002,
                            R.drawable.archer_walk_003,
                            R.drawable.archer_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.archer_run_000,
                            R.drawable.archer_run_001,
                            R.drawable.archer_run_002,
                            R.drawable.archer_run_003,
                            R.drawable.archer_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.archer_attack_000,
                            R.drawable.archer_attack_003,
                            R.drawable.archer_attack_005,
                            R.drawable.archer_attack_007,
                            R.drawable.archer_attack_009
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.archer_die_000,
                            R.drawable.archer_die_002,
                            R.drawable.archer_die_004,
                            R.drawable.archer_die_006,
                            R.drawable.archer_die_009
                    ))
            );
        }
    }

    public static class Mage extends Lawful {
        public Mage() {
            super(Id.Lawful.MAGE.ordinal(), "Mage","das", R.drawable.mage_idle_000,
                    new Status(50, 50, 30, 0, 0, 1, 1, 1, 2),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.mage_idle_000,
                            R.drawable.mage_idle_001,
                            R.drawable.mage_idle_002,
                            R.drawable.mage_idle_003,
                            R.drawable.mage_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.mage_walk_000,
                            R.drawable.mage_walk_001,
                            R.drawable.mage_walk_002,
                            R.drawable.mage_walk_003,
                            R.drawable.mage_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.mage_run_000,
                            R.drawable.mage_run_001,
                            R.drawable.mage_run_002,
                            R.drawable.mage_run_003,
                            R.drawable.mage_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.mage_attack_000,
                            R.drawable.mage_attack_004,
                            R.drawable.mage_attack_006,
                            R.drawable.mage_attack_008,
                            R.drawable.mage_attack_009
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.mage_die_000,
                            R.drawable.mage_die_002,
                            R.drawable.mage_die_004,
                            R.drawable.mage_die_006,
                            R.drawable.mage_die_008
                    ))
            );
        }
    }

    public static class SwordMan extends Lawful {
        public SwordMan() {
            super(Id.Lawful.SWORDMAN.ordinal(), "Sword Man", "dea", R.drawable.swordman_idle_000,
                    new Status(100, 100, 30, 20, 5, 1, 0, 0, 1),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.swordman_idle_000,
                            R.drawable.swordman_idle_001,
                            R.drawable.swordman_idle_002,
                            R.drawable.swordman_idle_003,
                            R.drawable.swordman_idle_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.swordman_walk_000,
                            R.drawable.swordman_walk_001,
                            R.drawable.swordman_walk_002,
                            R.drawable.swordman_walk_003,
                            R.drawable.swordman_walk_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.swordman_run_000,
                            R.drawable.swordman_run_001,
                            R.drawable.swordman_run_002,
                            R.drawable.swordman_run_003,
                            R.drawable.swordman_run_004
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.swordman_attack_000,
                            R.drawable.swordman_attack_002,
                            R.drawable.swordman_attack_003,
                            R.drawable.swordman_attack_004,
                            R.drawable.swordman_attack_005
                    )),
                    new ArrayList<>(Arrays.asList(
                            R.drawable.swordman_die_000,
                            R.drawable.swordman_die_003,
                            R.drawable.swordman_die_005,
                            R.drawable.swordman_die_007,
                            R.drawable.swordman_die_009
                    ))
            );
        }
    }
}