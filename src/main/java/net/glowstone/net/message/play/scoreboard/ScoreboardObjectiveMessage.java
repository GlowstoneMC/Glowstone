package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;

public final class ScoreboardObjectiveMessage implements Message {

    private final String name;
    private final String displayName;
    private final int action;

    public enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }

    public ScoreboardObjectiveMessage(String name, String displayName, Action action) {
        this.name = name;
        this.displayName = displayName;
        this.action = action.ordinal();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAction() {
        return action;
    }
}

