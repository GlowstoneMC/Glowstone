package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class JoinGameMessage implements Message {

    private final int id, dimension, mode, difficulty, maxPlayers;
    private final String levelType;

    public JoinGameMessage(int id, int mode, int dimension, int difficulty, int maxPlayers, String levelType) {
        this.id = id;
        this.mode = mode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        // default, flat, largeBiomes, amplified, default_1_1
    }

    public int getId() {
        return id;
    }

    public int getGameMode() {
        return mode;
    }

    public int getDimension() {
        return dimension;
    }
    
    public int getDifficulty() {
        return difficulty;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLevelType() {
        return levelType;
    }

    @Override
    public String toString() {
        return "JoinGameMessage{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", mode=" + mode +
                ", difficulty=" + difficulty +
                ", maxPlayers=" + maxPlayers +
                ", levelType='" + levelType + '\'' +
                '}';
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
