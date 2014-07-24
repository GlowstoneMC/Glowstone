package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;

public final class ScoreboardDisplayMessage implements Message {

    private final int position;
    private final String objective;

    public ScoreboardDisplayMessage(int position, String objective) {
        this.position = position;
        this.objective = objective;
    }

    public int getPosition() {
        return position;
    }

    public String getObjective() {
        return objective;
    }

    @Override
    public String toString() {
        return "ScoreboardDisplayMessage{" +
                "position=" + position +
                ", objective='" + objective + '\'' +
                '}';
    }
}

