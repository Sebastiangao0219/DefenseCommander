package com.sebastian.defensecommander;

public class Player {
    private final String init;
    private final int level;
    private final int score;
    private final String time;

    public Player(String init, int level, int score, String time) {
        this.init = init;
        this.level = level;
        this.score = score;
        this.time = time;
    }

    public int getScore() {
        return score;
    }
}
