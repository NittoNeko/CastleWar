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
        private BattleField battleField;

        public Tile(int id, int x, int y, BattleField battleField) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.battleField = battleField;
        }

        public BattleField getBattleField() {
            return battleField;
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

        public boolean hasUnit()
        {
            if (unit == null) return true;
            else return false;
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

        public BattleField(int id, int length) {
            this.id = id;
            this.length = length;
            this.tileNum = length / Ally.PIXEL;
            this.tiles = new Tile[tileNum];
            for (int i = 0; i < length / Ally.PIXEL; ++i)
            {
                this.tiles[i] = new Tile(i, id * length + i * Ally.PIXEL, SystemData.getGroundLine() - Ally.PIXEL, this);
            }
        }

        public Tile findFirstAvailableTile()
        {
            for (Tile tile : tiles)
            {
                if (!tile.hasUnit())
                    return tile;
            }
            return null;
        }

        public int getAvailableTileNum()
        {
            int num = 0;
            for (int i = 0; i < tileNum; ++i)
            {
                if (!tiles[i].hasUnit()) num++;
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

    // NOTE!!! battleFieldNum must be greater than 1
    public Terrain(Id id, String name, int resource, int battleFieldLength, int battleFieldNum) {
        super(id, name, resource);
        this.battleFieldNum = battleFieldNum;
        this.battleFieldLength = battleFieldLength;
        this.battleFieldsWidth = battleFieldLength * (battleFieldNum + 2);
        battleFields = new BattleField[battleFieldNum];
        for (int i = 0; i < battleFieldNum; ++i)
        {
            if (i == 0 || i == battleFieldNum - 1) battleFields[i] = new BattleField(i, battleFieldLength * 2);
            battleFields[i] = new BattleField(i, battleFieldLength);
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

    public static class Forest extends Terrain
    {
        public Forest() {
            super(Id.FOREST, "Forest", R.drawable.forest_ground, 500, 5);
            setY(SystemData.getGroundLine() - 30);
        }
    }
}
