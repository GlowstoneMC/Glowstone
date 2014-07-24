package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;

public final class ScoreboardObjectiveMessage implements Message {

    private final String name;
    private final String displayName;
    private final int action;

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }

    private ScoreboardObjectiveMessage(String name, String displayName, Action action) {
        this.name = name;
        this.displayName = displayName;
        this.action = action.ordinal();
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE);
    }

    public static ScoreboardObjectiveMessage remove(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.REMOVE);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE);
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

    @Override
    public String toString() {
        return "ScoreboardObjectiveMessage{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", action=" + action +
                '}';
    }
}

