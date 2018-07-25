package com.shiv.gastimate;

/*
 * Created by Shiv Paul on 6/27/2018.
 */

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class Constants
{

    //Used for location and location search activities
    public static final int FROM_LOCATION_REQUEST = 0;
    public static final int TO_LOCATION_REQUEST = 1;

    //used to identify vehicle type
    public static final int CAR = 0;
    public static final int MOTORCYCLE = 1;
    public static final int OTHER = 2;

    //used to see if vehicle fuel is being tracked or not
    public static final int TRACKING = 1;
    public static final int NOT_TRACKING = 0;

    //Duration for ValueAnimator
    static final int animationDuration = 1170;

    /**
     * Animates the given TextView
     * @param initialValue, the starting value of animation
     * @param finalValue, the ending value of animation
     * @param textView, the TextView to be animated
     */
    @SuppressLint("DefaultLocale")
    public static void animateTextView(float initialValue, float finalValue, final TextView textView)
    {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator((float) 10));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                textView.setText(String.format("%2.2f", (Float) valueAnimator.getAnimatedValue()));
            }
        });
        valueAnimator.start();
    }
}
