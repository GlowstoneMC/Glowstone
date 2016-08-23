package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoreboardScorePacket implements Message {

    private final String target;
    private final boolean remove;
    private final String objective;
    private final int value;

    public ScoreboardScorePacket(String target, String objective, int value) {
        this(target, false, objective, value);
    }

    public static ScoreboardScorePacket remove(String target, String objective) {
        return new ScoreboardScorePacket(target, true, objective, 0);
    }
}
