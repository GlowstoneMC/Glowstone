package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.scoreboard.RenderType;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoreboardObjectivePacket implements Message {

    private final String name;
    private final String displayName;
    private final int action;
    private final RenderType renderType;

    public static ScoreboardObjectivePacket create(String name, String displayName) {
        return new ScoreboardObjectivePacket(name, displayName, Action.CREATE.ordinal(), RenderType.INTEGER);
    }

    public static ScoreboardObjectivePacket create(String name, String displayName, RenderType renderType) {
        return new ScoreboardObjectivePacket(name, displayName, Action.CREATE.ordinal(), renderType);
    }

    public static ScoreboardObjectivePacket remove(String name) {
        return new ScoreboardObjectivePacket(name, null, Action.REMOVE.ordinal(), null);
    }

    public static ScoreboardObjectivePacket update(String name, String displayName) {
        return new ScoreboardObjectivePacket(name, displayName, Action.UPDATE.ordinal(), RenderType.INTEGER);
    }

    public static ScoreboardObjectivePacket update(String name, String displayName, RenderType renderType) {
        return new ScoreboardObjectivePacket(name, displayName, Action.UPDATE.ordinal(), renderType);
    }

    private enum Action {
        CREATE,
        REMOVE,
        UPDATE
    }
}
