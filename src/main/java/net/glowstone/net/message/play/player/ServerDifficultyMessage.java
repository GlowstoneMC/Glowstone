package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class ServerDifficultyMessage implements Message {

    private final int difficulty;

    public ServerDifficultyMessage(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return "ServerDifficultyMessage{" +
                "difficulty=" + difficulty +
                '}';
    }
}
