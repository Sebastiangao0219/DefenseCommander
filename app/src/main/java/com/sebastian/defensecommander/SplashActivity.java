package com.sebastian.defensecommander;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashActivity extends AppCompatActivity {
    private int screenHeight;
    private int screenWidth;
    private static final int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SoundPlayer.getInstance().startBackgroundSounds(this);
        setupTitle();
        splashToMain();
    }

    private void splashToMain() {
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_TIME_OUT);
    }

    private void setupTitle(){
        getScreenDimensions();
        ImageView titleView = new ImageView(this);
        ConstraintLayout layout = findViewById(R.id.splash_layout);
        titleView.setImageResource(R.drawable.title);

        float w = titleView.getDrawable().getIntrinsicWidth();
        float h = titleView.getDrawable().getIntrinsicHeight();

        titleView.setX(screenWidth/2 - w/2);
        titleView.setY(screenHeight/2 - h/2);
        layout.addView(titleView);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(titleView, "alpha", 0.0f, 1.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(SPLASH_TIME_OUT);
        alpha.start();

    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        Utils.setScreenHeightAndWidth(screenHeight,screenWidth);
    }
}
