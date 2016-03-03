package net.glowstone.net.message.play.scoreboard;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ScoreboardDisplayMessage implements Message {

    private final int position;
    private final String objective;

}
