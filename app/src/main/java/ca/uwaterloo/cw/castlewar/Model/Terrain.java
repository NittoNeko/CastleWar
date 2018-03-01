package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/16.
 */

public class Terrain extends GameObject {
    public static class Tile {
        private Unit unit;
        private int id;
        private int x;
        private int y;
        private BattleField parent;

        public Tile(int id, int x, int y, BattleField battleField) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.parent = battleField;
            this.unit = null;
        }

        public BattleField getParent() {
            return parent;
        }

        public int getParentId()
        {
            return parent.getId();
        }

        public int getId() {
            return id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isAvailable()
        {
            if (unit == null) return true;
            else return false;
        }

        public Unit getUnit() {
            return unit;
        }

        public void setUnit(Unit unit)
        {
            this.unit = unit;
        }
    }

    public static class BattleField {
        private int id;
        private int length;
        private Tile[] tiles;
        private int tileNum;
        private Terrain terrain;

        public BattleField(int id, int length, Terrain terrain) {
            this.id = id;
            this.length = length;
            this.tileNum = length / SystemData.PIXEL;
            this.tiles = new Tile[tileNum];
            this.terrain = terrain;
            for (int i = 0; i < length / SystemData.PIXEL; ++i)
            {
                if (id == 0) this.tiles[i] = new Tile(i,  i * SystemData.PIXEL, SystemData.getGroundLine() - SystemData.PIXEL, this);
                else if (id == terrain.battleFieldNum - 1) this.tiles[i] = new Tile(i, id * (length - Castle.SIZE) + Castle.SIZE + i * SystemData.PIXEL, SystemData.getGroundLine() - SystemData.PIXEL, this);
                else this.tiles[i] = new Tile(i, id * length + Castle.SIZE + i * SystemData.PIXEL, SystemData.getGroundLine() - SystemData.PIXEL, this);
            }
        }

        public Tile[] getReversedTiles()
        {
            Tile[] reverse = new Tile[tiles.length];
            for (int i = tiles.length - 1; i >= 0; --i)
                reverse[i] = tiles[tiles.length - i - 1];
            return reverse;
        }

        public Tile findFirstAvailableTile(boolean isPlayer1)
        {
            for (Tile tile : isPlayer1 ? tiles : getReversedTiles()){
                if (tile.isAvailable()){
                    return tile;
                }
            }
            return null;
        }

        public int getAvailableTileNum()
        {
            int num = 0;
            for (Tile tile : tiles) {
                if (tile.isAvailable()) {
                    num++;
                }
            }
            return num;
        }

        public int getId() {
            return id;
        }

        public int getLength() {
            return length;
        }

        public Tile[] getTiles() {
            return tiles;
        }
    }

    private BattleField[] battleFields;
    private int battleFieldLength;
    private int battleFieldNum;
    private int battleFieldsWidth;
    private int castleLength;

    // NOTE!!! battleFieldNum must be greater than 1
    public Terrain(int id, String name, int resource, int battleFieldLength, int battleFieldNum) {
        super(id, name, resource);
        this.battleFieldNum = battleFieldNum;
        this.battleFieldLength = battleFieldLength;
        this.castleLength = (int) Castle.SIZE;
        int extraLength = castleLength + battleFieldLength;
        this.battleFieldsWidth = battleFieldLength * battleFieldNum + 2 * castleLength;
        battleFields = new BattleField[battleFieldNum];
        for (int i = 0; i < battleFieldNum; ++i)
        {
            if (i == 0 || i == battleFieldNum - 1) battleFields[i] = new BattleField(i, extraLength, this);
            else battleFields[i] = new BattleField(i, battleFieldLength, this);
        }
    }

    public BattleField[] getBattleFields() {
        return battleFields;
    }

    public int getBattleFieldLength() {
        return battleFieldLength;
    }

    public int getBattleFieldNum() {
        return battleFieldNum;
    }

    public int getBattleFieldsWidth() {
        return battleFieldsWidth;
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        float ratio = battleFieldsWidth / SystemData.getScreenWidth();
        setPortrait(createScaledBitmap(original, battleFieldsWidth, (int) (ratio * SystemData.getScreenHeight()), false));
    }

    public BattleField[] getReversedBattlefield()
    {
        BattleField[] reverse = new BattleField[battleFieldNum];
        for (int i = battleFieldNum - 1; i >= 0; --i)
            reverse[i] = battleFields[battleFieldNum - i - 1];
        return reverse;
    }

    public static class Forest extends Terrain
    {
        public Forest() {
            super(Id.Terrain.FOREST.ordinal(), "Forest", R.drawable.forest_ground, 500, 4);
            setY(SystemData.getGroundLine() - 50);
        }
    }
}
