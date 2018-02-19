package ca.uwaterloo.cw.castlewar.Activity;

import android.content.Context;
import android.view.SurfaceView;

import java.util.ArrayList;

import ca.uwaterloo.cw.castlewar.Model.Ally;
import ca.uwaterloo.cw.castlewar.Model.Enemy;
import ca.uwaterloo.cw.castlewar.Model.Level;

/**
 * Created by harrison33 on 2018/2/19.
 */

public class GameLogic extends SurfaceView implements Runnable {
    private ArrayList<Ally> allies;
    private ArrayList<Enemy> enemies;

    public GameLogic(Context context, Level level) {
        super(context);
        allies = new ArrayList<>()
    }

    public void run()
    {

    }
}
