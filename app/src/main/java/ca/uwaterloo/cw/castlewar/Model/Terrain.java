package ca.uwaterloo.cw.castlewar.Model;

/**
 * Created by harri on 2018/2/16.
 */

public class Terrain extends GameObject {
    public Terrain(int id, String name) {
        super(id, name, SystemData.TypeId.TERRAIN.id());
    }



    public static class Forest extends Terrain
    {
        public Forest() {
            super(SystemData.TerrainId.FOREST.id(), "Forest");
        }
    }
}
