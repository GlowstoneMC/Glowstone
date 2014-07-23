package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;

public final class ScoreboardScoreMessage implements Message {

    private final String target;
    private final boolean remove;
    private final String objective;
    private final int value;

    private ScoreboardScoreMessage(String target, boolean remove, String objective, int value) {
        this.target = target;
        this.remove = remove;
        this.objective = objective;
        this.value = value;
    }

    public ScoreboardScoreMessage(String target, String objective, int value) {
        this(target, false, objective, value);
    }

    public static ScoreboardScoreMessage remove(String target) {
        return new ScoreboardScoreMessage(target, true, null, 0);
    }

    public String getTarget() {
        return target;
    }

    public boolean isRemove() {
        return remove;
    }

    public String getObjective() {
        return objective;
    }

    public int getValue() {
        return value;
    }
}

