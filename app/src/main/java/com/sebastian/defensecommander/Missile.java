package com.sebastian.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {
    private final MainActivity mainActivity;
    private final long screenTime;

    private ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();

    public Missile(long screenTime, final MainActivity mainActivity) {
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;

        initialize();
    }

    public void initialize() {
        imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.missile);

        float w = imageView.getDrawable().getIntrinsicWidth() / 2;
        float startX = (int) (Math.random() * Utils.screenWidth) - w ;
        float endX = (int) (Math.random() * Utils.screenWidth);

        float startY = -100 - w;
        float endY = Utils.screenHeight ;

        float rotationAngle = calculateAngle(startX, startY, endX, endY);

        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);
        imageView.setRotation(rotationAngle);

        mainActivity.getLayout().addView(imageView);

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "translationX", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);


        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "translationY", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);

        xAnim.addUpdateListener(animation -> {
            if (imageView.getY() > (Utils.screenHeight * 0.85)){
                aSet.cancel();
                makeGroundBlast();
                mainActivity.removeMissile(Missile.this);
            }
        });
    }

    public ImageView getMissileImage(){
        return imageView;
    }

    public void launch() {
        aSet.start();
    }

    private void makeGroundBlast() {
        SoundPlayer.getInstance().start("missile_miss");
        final ImageView exlodeImage = new ImageView(mainActivity);
        exlodeImage.setImageResource(R.drawable.explode);
        exlodeImage.setX(getX()  );
        exlodeImage.setY(getY() );
        exlodeImage.setZ(-15);

        mainActivity.getLayout().addView(exlodeImage);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(exlodeImage, "alpha", 1.0f, 0.0f);
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(exlodeImage);
            }
        });
        alpha.start();
        mainActivity.applyMissileBlast(getX(), getY());

    }

    float getX() {
        return imageView.getX();
    }
    float getY() {
        return imageView.getY();
    }

    public float calculateAngle(float x1, float y1, float x2, float y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;

        return (float) (190.0f - angle);

    }

    void interceptorBlast() {
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setX(getX());
        iv.setY(getY());
        aSet.cancel();
        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 1.0f, 0.0f);
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
    }
}
