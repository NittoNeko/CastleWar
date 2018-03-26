package ca.uwaterloo.cw.castlewar.Base;

import android.graphics.Bitmap;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/3/25.
 */

abstract public class Icon {
    public static final Bitmap empty;

    static {
        empty = System.scaleBitmap(R.drawable.cross, 100, 100, 2);
    }

}
