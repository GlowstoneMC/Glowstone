package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;

public final class ScoreboardObjectiveMessage implements Message {

    private final String name;
    private final String displayName;
    private final int action;
    private final String type;

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }

    private ScoreboardObjectiveMessage(String name, String displayName, Action action, String type) {
        this.name = name;
        this.displayName = displayName;
        this.action = action.ordinal();
        this.type = type;
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE, "integer");
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName, boolean useHearts) {
        String type = "integer";
        if (useHearts) {
            type = "hearts";
        }
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE, type);
    }

    public static ScoreboardObjectiveMessage remove(String name) {
        return new ScoreboardObjectiveMessage(name, null, Action.REMOVE, null);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE, "integer");
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName, boolean useHearts) {
        String type = "integer";
        if (useHearts) {
            type = "hearts";
        }
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE, type);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ScoreboardObjectiveMessage{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + type + '\'' +
                ", action=" + action +
                '}';
    }
}

