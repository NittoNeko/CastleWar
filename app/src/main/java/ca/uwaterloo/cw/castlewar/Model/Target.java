package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by harri on 2018/2/23.
 */

public class Target extends GameObject {
    public Target() {
        super("Target", SystemData.TypeId.FUNCTION, BitmapFactory.decodeResource(SystemData.getContext().getResources(), R.drawable.target));
    }
}
