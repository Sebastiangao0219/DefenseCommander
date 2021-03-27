package com.sebastian.defensecommander;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static com.sebastian.defensecommander.Utils.screenHeight;
import static com.sebastian.defensecommander.Utils.screenWidth;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout layout;
    private MissileMaker missileMaker;
    private final ArrayList<Base> basesList = new ArrayList<>();
    private final ArrayList<Missile> missileList = new ArrayList<>();
    private TextView score, level;
    private ImageView image1, image2, image3;
    private String stringFromDatabase = null;
    public static String initialName = null;
    private int scoreValue;
    private final ArrayList<Interceptor> activeInterceptors = new ArrayList<>();
    private int levelValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Utils.setupFullScreen(this);
        loadSounds();
        setupBases();
    }

    public void init() {
        layout = findViewById(R.id.mainLayout);
        score = findViewById(R.id.scoreText);
        level = findViewById(R.id.levelText);
        image1 = findViewById(R.id.imageView1);
        image2 = findViewById(R.id.imageView2);
        image3 = findViewById(R.id.imageView3);

        new CloudBackground(this, layout, 4000);

        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        missileMaker = new MissileMaker(this);
        new Thread(missileMaker).start();
    }

    public void setupBases() {
        Base base1 = new Base(this, image1);
        Base base2 = new Base(this, image2);
        Base base3 = new Base(this, image3);

        basesList.add(base1);
        basesList.add(base2);
        basesList.add(base3);
    }

    public void handleTouch(float x, float y) {
        if (basesList.isEmpty())
            return;
        Base base = getClosestBase(x, y);
        launchInterceptor(base, x, y);
    }

    private void launchInterceptor(Base base, float x, float y) {
        if (activeInterceptors.size() < 3) {
            Interceptor i = new Interceptor(this, base, x, y);
            SoundPlayer.getInstance().start("launch_interceptor");
            i.launch();
            activeInterceptors.add(i);
        }
    }

    public void removeInterceptor(Interceptor interceptor) {
        activeInterceptors.remove(interceptor);
    }

    public void loadSounds() {
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile);
        SoundPlayer.getInstance().setupSound(this, "missile_miss", R.raw.missile_miss);
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void incrementScore() {
        scoreValue += 100;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void setLevel(int value) {
        levelValue = value;
        level.setText(String.format(Locale.getDefault(), "Level: %d", value));
    }

    public void addMissile(Missile missile) {
        missileList.add(missile);
    }

    public void removeMissile(Missile missile) {
        layout.removeView(missile.getMissileImage());
        missileList.remove(missile);
    }

    public void applyMissileBlast(float x, float y) {
        if (basesList.isEmpty()) {
            endGame();
        } else {
            Base base = getClosestBase(x, y);
            assert base != null;
            double distance = getDistance(x, y, base.getX(), base.getY());

            if (distance < 200) {
                basesList.remove(base);
                base.destroyed();
                if (basesList.isEmpty()) {
                    endGame();
                }
            }
        }
    }

    public void applyInterceptorBlast(float x, float y) {
        if (basesList.isEmpty()) {
            endGame();
        } else {
            Base base = getClosestBase(x, y);
            assert base != null;
            double baseDistance = getDistance(x, y, base.getX(), base.getY());

            if (baseDistance < 120) {
                basesList.remove(base);
                base.destroyed();

                if (basesList.isEmpty()) {
                    endGame();
                }
            }
        }

        if (!missileList.isEmpty()) {
            for (int i = 0; i < missileList.size(); i++) {
                Missile missile = missileList.get(i);
                double distance = getDistance(x, y, missile.getX(), missile.getY());
                if (distance < 120) {
                    incrementScore();
                    SoundPlayer.getInstance().start("interceptor_hit_missile");
                    missile.interceptorBlast();
                    removeMissile(missile);
                }
            }
        }
    }

    private void endGame() {
        missileMaker.setRunning(false);
        showGameOver();
        new Handler().postDelayed(this::doDatabaseQuery, 3000);
    }

    private void showGameOver() {
        ImageView titleView = new ImageView(this);
        titleView.setImageResource(R.drawable.game_over);

        float w = titleView.getDrawable().getIntrinsicWidth();
        float h = titleView.getDrawable().getIntrinsicHeight();

        titleView.setX(screenWidth / 2 - w / 2);
        titleView.setY(screenHeight / 2 - h / 2);
        layout.addView(titleView);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(titleView, "alpha", 0.0f, 1.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.start();
    }

    public void doDatabaseQuery() {
        ScoreDatabaseHandler sdb = new ScoreDatabaseHandler(this, initialName, scoreValue, levelValue);
        new Thread(sdb).start();
    }

    public void doDatabaseInsert(String name) {
        initialName = name;
        doDatabaseQuery();
    }

    public void setResultsFromDatabase(String s) {
        stringFromDatabase = s;
        goToScoreActivity();
    }

    public void goToScoreActivity() {
        if (stringFromDatabase != null) {
            Intent intent = new Intent(this, TopScoreActivity.class);
            intent.putExtra("SCOREINFO", stringFromDatabase);
            startActivity(intent);
            finish();
        }
    }

    private Base getClosestBase(float x, float y) {
        if (basesList.isEmpty()) {
            return null;
        }

        double minDistance = getDistance(x, y, basesList.get(0).getX(), basesList.get(0).getY());
        int index = 0;

        for (int i = 1; i < basesList.size(); i++) {
            double value = getDistance(x, y, basesList.get(i).getX(), basesList.get(i).getY());
            if (value < minDistance) {
                minDistance = value;
                index = i;
            }
        }
        return basesList.get(index);
    }

    public double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}