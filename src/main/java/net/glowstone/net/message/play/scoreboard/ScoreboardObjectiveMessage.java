package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;
import org.bukkit.scoreboard.RenderType;

public final class ScoreboardObjectiveMessage implements Message {

    private final String name;
    private final String displayName;
    private final int action;
    private final RenderType renderType;

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }

    private ScoreboardObjectiveMessage(String name, String displayName, Action action, RenderType renderType) {
        this.name = name;
        this.displayName = displayName;
        this.action = action.ordinal();
        this.renderType = renderType;
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE, RenderType.INTEGER);
    }

    public static ScoreboardObjectiveMessage create(String name, String displayName, RenderType renderType) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.CREATE, renderType);
    }

    public static ScoreboardObjectiveMessage remove(String name) {
        return new ScoreboardObjectiveMessage(name, null, Action.REMOVE, null);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE, RenderType.INTEGER);
    }

    public static ScoreboardObjectiveMessage update(String name, String displayName, RenderType renderType) {
        return new ScoreboardObjectiveMessage(name, displayName, Action.UPDATE, renderType);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ScoreboardObjectiveMessage{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", renderType='" + renderType + '\'' +
                ", action=" + action +
                '}';
    }
}

