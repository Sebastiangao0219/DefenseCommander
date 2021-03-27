package com.sebastian.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

public class Base {
    private final MainActivity mainActivity;
    private final ImageView imageView;


    public Base(MainActivity mainActivity, ImageView imageView) {
        this.mainActivity = mainActivity;
        this.imageView = imageView;
    }

    public float getX(){
        return (float) (imageView.getX() + (0.5 * imageView.getWidth()));
    }
    public float getY(){
        return (float) (imageView.getY() + (0.5 * imageView.getHeight()));
    }

    public void destroyed() {
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(imageView);

        final ImageView blastImage = new ImageView(mainActivity);
        blastImage.setImageResource(R.drawable.blast);

        float w = (float) (blastImage.getDrawable().getIntrinsicWidth() * 0.5);

        blastImage.setX( getX() - w);
        blastImage.setY( getY() - w);

        mainActivity.getLayout().addView(blastImage);


        final ObjectAnimator alpha = ObjectAnimator.ofFloat(blastImage, "alpha", 1.0f, 0.0f);
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(blastImage);
            }
        });
        alpha.start();
    }
}
