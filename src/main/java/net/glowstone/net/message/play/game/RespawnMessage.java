package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class RespawnMessage implements Message {

    private final int dimension, difficulty, mode;
    private final String levelType;

    public RespawnMessage(int dimension, int difficulty, int mode, String levelType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.mode = mode;
        this.levelType = levelType;
    }

    public int getDimension() {
        return dimension;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getMode() {
        return mode;
    }

    public String getLevelType() {
        return levelType;
    }

    @Override
    public String toString() {
        return "RespawnMessage{" +
                "dimension=" + dimension +
                ", difficulty=" + difficulty +
                ", mode=" + mode +
                ", levelType='" + levelType + '\'' +
                '}';
    }
}
