package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/16.
 */

public class Terrain extends GameObject {
    private SystemData.TerrainId id;

    public Terrain(SystemData.TerrainId id, String name, int resource) {
        super(name, SystemData.TypeId.TERRAIN, resource);
    }

    public static class Forest extends Terrain
    {
        public Forest() {
            super(SystemData.TerrainId.FOREST, "Forest", R.drawable.forest_ground);
            setY(SystemData.getGroundLine());
        }
    }
}
