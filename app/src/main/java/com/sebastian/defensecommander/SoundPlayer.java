package com.sebastian.defensecommander;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.HashSet;

public class SoundPlayer {
    private static SoundPlayer instance;
    private final SoundPool soundPool;
    private static final int MAX_STREAMS = 10;
    private final HashSet<Integer> loaded = new HashSet<>();
    private final HashSet<String> loopList = new HashSet<>();
    private final HashMap<String, Integer> soundNameToResource = new HashMap<>();
    private  MediaPlayer mp;

    private SoundPlayer() {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();
        this.soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> loaded.add(sampleId));
    }

    static SoundPlayer getInstance() {
        if (instance == null)
            instance = new SoundPlayer();
        return instance;
    }

    void setupSound(Context context, String id, int resource) {
        int soundId = soundPool.load(context, resource, 1);
        soundNameToResource.put(id, soundId);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start(final String id) {
        if (!loaded.contains(soundNameToResource.get(id))) {
            return;
        }
        int loop = 0;
        if (loopList.contains(id))
            loop = -1;
        soundPool.play(soundNameToResource.get(id), 1f, 1f, 1, loop, 1f);
    }

    public void startBackgroundSounds(Context context) {
        mp = MediaPlayer.create(context, R.raw.background);
        mp.setLooping(true);
        mp.start();
    }
    public void stopBackgroundSounds(){
        mp.stop();
    }
}
