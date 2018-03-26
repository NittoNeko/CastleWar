package ca.uwaterloo.cw.castlewar.Base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.widget.TextView;

import java.util.HashMap;

import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.Structure.Id;

/**
 * Created by harri on 2018/3/25.
 */

public class Animation extends ValueAnimator{

    public Animation() {
        super();
        this.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GameManager.instance().addAnimation(Animation.this);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                GameManager.instance().removeAnimation(Animation.this);
            }
        });
    }

    public static void explode() {
        Animation animation = new Animation();
        animation.setIntValues();
    }
}
