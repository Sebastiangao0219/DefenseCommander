package com.sebastian.defensecommander;

public class MissileMaker implements Runnable {
    private final MainActivity mainActivity;
    private boolean isRunning;
    private int count = 0;
    private static final int LEVEL_COUNT = 5;
    private int currentLevel = 1;
    private long delayBetweenMissiles = 4000;

    MissileMaker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    void setRunning(boolean running) {
        isRunning = running;
    }
    @Override
    public void run() {
        setRunning(true);
        try {
            Thread.sleep((long) (delayBetweenMissiles * 0.5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            mainActivity.runOnUiThread(this::makeMissile);
            count++;
            if (count > LEVEL_COUNT) {
                increasedLevel();
                count = 0;
            }
            long sleepTime = getSleepTime();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeMissile(){
        Missile missile = new Missile(delayBetweenMissiles, mainActivity);
        mainActivity.addMissile(missile);
        SoundPlayer.getInstance().start("launch_missile");
        missile.launch();
    }

    public void increasedLevel(){
        currentLevel++;
        delayBetweenMissiles -= 500;
        if (delayBetweenMissiles <= 0) {
            delayBetweenMissiles = 1;
        }

        mainActivity.runOnUiThread(() -> mainActivity.setLevel(currentLevel));

        try {
            Thread.sleep((long) (2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getSleepTime() {
        double num = Math.random();
        if (num < 0.1){
            return 1;
        } else if (num < 0.2){
            return (long) (0.5 * delayBetweenMissiles);
        } else {
            return delayBetweenMissiles;
        }
    }
}
