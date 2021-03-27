package com.sebastian.defensecommander;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CloudBackground {
    private final Context context;
    private final ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private final long duration;

    public CloudBackground(Context context, ViewGroup layout, long duration) {
        this.context = context;
        this.layout = layout;
        this.duration = duration;
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(Utils.screenWidth + getBarHeight(), Utils.screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        backImageA.setImageResource(R.drawable.clouds);
        backImageB.setImageResource(R.drawable.clouds);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(-1);
        backImageB.setZ(-1);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(backImageA, "alpha", 0.25f, 0.9f, 0.25f);
        alpha.setDuration(duration);
        alpha.setRepeatMode(ValueAnimator.RESTART);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        alpha.start();


        final ObjectAnimator alpha1 = ObjectAnimator.ofFloat(backImageB, "alpha", 0.25f, 0.9f, 0.25f);
        alpha1.setDuration(duration);
        alpha1.setRepeatMode(ValueAnimator.RESTART);
        alpha1.setRepeatCount(ValueAnimator.INFINITE);

        alpha1.start();
        animateBack();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();
            float width = Utils.screenWidth + getBarHeight();

            float a_translationX = width * progress;
            float b_translationX = width * progress - width;

            backImageA.setTranslationX(a_translationX);
            backImageB.setTranslationX(b_translationX);
        });
        animator.start();
    }

    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
