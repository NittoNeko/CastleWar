package ca.uwaterloo.cw.castlewar.Base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Structure.CombatView;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Unit.Unit;

/**
 * Created by harri on 2018/3/25.
 */

public class Animation extends ValueAnimator {
    private static AtomicInteger VALUE_PER_SECOND = new AtomicInteger(15);  // this is speed of text
    private static AtomicInteger FRAME_PER_SECOND = new AtomicInteger(15); // speed of animation
    private static final long MILISECOND = 1000;
    private AtomicBoolean hasStart = new AtomicBoolean(false);
    private static final HashMap<Id.CombatRole, ArrayList<Bitmap>> animations = new HashMap<>();

    public Animation() {
        super();
        this.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GameManager.instance().removeAnimation(Animation.this);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                GameManager.instance().addAnimation(Animation.this);
                Animation.this.hasStart.set(true);
            }
        });
    }

    public static void initializeAnimations(Id.Attack attackerType, Id.Attack defenderType) {
        animations.put(Id.CombatRole.ATTACKER, new ArrayList<Bitmap>());
        animations.put(Id.CombatRole.DEFENDER, new ArrayList<Bitmap>());
        initializeAnimation(attackerType, Id.CombatRole.ATTACKER);
        initializeAnimation(defenderType, Id.CombatRole.DEFENDER);
    }

    private static void initializeAnimation(Id.Attack attack, Id.CombatRole role) {
        switch (attack) {
            case HIT: readAnimation(R.drawable.effect_hit, 1, 5,2, role); break;
            case ICE_BALST: readAnimation(R.drawable.effect_ice_blast, 3, 5, 0, role); break;
            case THUNDER: readAnimation(R.drawable.effect_thunder, 3, 5, 0, role); break;
            case WATER: readAnimation(R.drawable.effect_water, 4, 5, 3, role); break;
            case CLAW: readAnimation(R.drawable.effect_claw, 4, 5, 3, role); break;
            case SLASH: readAnimation(R.drawable.effect_slash, 3, 5, 1, role); break;
            case SLASH_FIRE: readAnimation(R.drawable.effect_slash_fire, 3, 5, 0, role); break;
            case ARROW: readAnimation(R.drawable.effect_arrow, 2, 5, 1, role); break;
        }
    }

    private static void readAnimation(int resource, int row, int column, int leftover, Id.CombatRole role) {
        Bitmap original = System.scaleBitmap(resource, null, null, 1);
        int width = original.getWidth() / column;
        int height = original.getHeight() / (leftover == 0 ? row : row + 1);

        for (int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                animations.get(role).add(Bitmap.createBitmap(original, j * width, i * height, width, height));
            }
        }

        for (int i = 0; i < leftover; ++i) {
            animations.get(role).add(Bitmap.createBitmap(original, i * width, row * height, width, height));
        }
    }

    public static void attackEffect(Id.CombatRole role, final ImageView view) {
        // play animation
        final ArrayList<Bitmap> effects = animations.get(role);
        final Animation animation = new Animation();
        int frame = effects.size();
        animation.setIntValues(0, frame - 1);
        long duration = frame * MILISECOND / FRAME_PER_SECOND.get();
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int position = (int) valueAnimator.getAnimatedValue();
                view.setImageBitmap(effects.get(position));
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setImageBitmap(null);
            }
        });
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });
        animation.waitForStart();
    }

    public static void healthEffect(int before, final int after, final CombatView combatView) {
        final Animation animation = new Animation();
        animation.setIntValues(before, after);
        Integer delta = Math.abs(before - after);
        long duration = delta * MILISECOND / VALUE_PER_SECOND.get();
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int hp = (int) valueAnimator.getAnimatedValue();
                combatView.getHp().setText(Integer.toString(hp));
                combatView.getHealthBar().setProgress(after);
            }
        });
        Animation.waitForAll();
        System.runOnUi(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });
        animation.waitForStart();
    }

    public void waitForStart() {
        while (!hasStart.get()) {
            if (!GameManager.instance().isGameActive()) return;
            // busy-waiting
            // bad!!!
        }
    }

    public static void waitForAll() {
        while (!GameManager.instance().isAnimationEmpty()) {
            if (!GameManager.instance().isGameActive()) return;
            // busy-waiting
            // bad!!!
        }
    }

    public static void setSpeed(boolean doubleSpeed) {
        if (doubleSpeed){
            VALUE_PER_SECOND.set(VALUE_PER_SECOND.get() * 2);
            FRAME_PER_SECOND.set(FRAME_PER_SECOND.get() * 2);
        } else {
            VALUE_PER_SECOND.set(VALUE_PER_SECOND.get() / 2);
            FRAME_PER_SECOND.set(FRAME_PER_SECOND.get() / 2);
        }
    }
}
