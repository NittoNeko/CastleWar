package ca.uwaterloo.cw.castlewar.Model;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/23.
 */

public class Coin extends GameObject {
    private long num;

    public Coin()
    {
        super(0, "Coin", R.drawable.ic_launcher_background);
        this.num = 0;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getNum() {
        return num;
    }
}
