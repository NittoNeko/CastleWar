package ca.uwaterloo.cw.castlewar.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.uwaterloo.cw.castlewar.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by harri on 2018/2/23.
 */

public class Target extends GameObject {
    public Target() {
        super(Id.TARGET_ICON,"Target", R.drawable.target);
    }

    @Override
    protected void createPortrait() {
        Bitmap original = BitmapFactory.decodeResource(SystemData.getContext().getResources(), getResource());
        setPortrait(createScaledBitmap(original, Ally.PIXEL, Ally.PIXEL, false));
        setY(SystemData.getGroundLine() - getPortrait().getHeight());
    }
}
