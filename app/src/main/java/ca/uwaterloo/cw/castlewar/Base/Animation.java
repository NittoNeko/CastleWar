package ca.uwaterloo.cw.castlewar.Base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.Structure.Id;

/**
 * Created by harri on 2018/3/25.
 */

public class Animation extends ValueAnimator {
    public static final long VALUE_PER_SECOND = 25;  // this is speed of text
    public static final long MILISECOND = 1000;

    private AtomicBoolean hasStart = new AtomicBoolean(false);


    public static void initializeAnimations() {

    }

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

    public static void explode() {
        Animation animation = new Animation();
        animation.setIntValues();
    }

    public void waitForStart() {
        while (!hasStart.get()) {
            // busy-waiting
            // bad!!!
        }
    }
}
