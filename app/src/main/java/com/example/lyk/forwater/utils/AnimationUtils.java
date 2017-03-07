package com.example.lyk.forwater.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by lyk on 2016/11/28.
 */

public class AnimationUtils {
    public static void setAnimation(View view, Interpolator interpolator)
    {
        Animation animation=new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setFillAfter(true);
        animation.setInterpolator(interpolator);
        animation.setDuration(3000);
        animation.setRepeatCount(Animation.INFINITE);
        view.setAnimation(animation);
        animation.start();

    }
}
