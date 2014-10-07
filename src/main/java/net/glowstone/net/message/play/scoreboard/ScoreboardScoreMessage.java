package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.networking.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoreboardScoreMessage implements Message {

    private final String target;
    private final boolean remove;
    private final String objective;
    private final int value;

    public ScoreboardScoreMessage(String target, String objective, int value) {
        this(target, false, objective, value);
    }

    public static ScoreboardScoreMessage remove(String target, String objective) {
        return new ScoreboardScoreMessage(target, true, objective, 0);
    }
}
