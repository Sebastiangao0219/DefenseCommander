package com.sebastian.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

public class Interceptor {
    private final MainActivity mainActivity;
    private final Base base;
    private ObjectAnimator moveX, moveY;
    private ImageView imageview;
    private float endX, endY;

    public Interceptor(MainActivity mainActivity, Base base, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.base = base;
        this.endX = endX;
        this.endY = endY;
        initialize();
    }

    private void initialize() {
        imageview = new ImageView(mainActivity);
        imageview.setImageResource(R.drawable.interceptor);

        final int www = (int) (imageview.getDrawable().getIntrinsicWidth() * 0.5);
        float startX = base.getX();
        float startY = Utils.screenHeight - 70;
        endX -= www;
        endY -= www;

        float rotatonAngle = calculateAngle(startX, startY, endX, endY);
        imageview.setX(startX);
        imageview.setY(startY);
        imageview.setZ(-10);
        imageview.setRotation(rotatonAngle);

        mainActivity.getLayout().addView(imageview);

        double distance =  Math.sqrt((endY - startY) * (endY - startY) + (endX - startX) * (endX - startX));
        moveX = ObjectAnimator.ofFloat(imageview, "x", startX, endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distance * 2));

        moveY = ObjectAnimator.ofFloat(imageview, "y", startY,endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distance * 2));

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview);
                makeBlast();
            }
        });
    }

    public void makeBlast() {
        SoundPlayer.getInstance().start("interceptor_blast");
        mainActivity.removeInterceptor(Interceptor.this);
        final ImageView explodeImage = new ImageView(mainActivity);
        explodeImage.setImageResource(R.drawable.i_explode);

        final int w = (int) (explodeImage.getDrawable().getIntrinsicWidth() * 0.5);

        float startX = getX() - w;
        float startY = getY() - w;
        explodeImage.setX(startX);
        explodeImage.setY(startY);
        explodeImage.setZ(-15);
        mainActivity.getLayout().addView(explodeImage);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeImage, "alpha", 1.0f, 0.0f);
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeImage);
            }
        });
        alpha.start();
        mainActivity.applyInterceptorBlast(explodeImage.getX(), explodeImage.getY());
    }

    void launch() {
        moveX.start();
        moveY.start();
    }

    float getX() {
        int xVar = imageview.getWidth() / 2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight() / 2;
        return imageview.getY() + yVar;
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);
    }
}
