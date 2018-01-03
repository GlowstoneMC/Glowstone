package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class JoinGameMessage implements Message {

    private final int id;
    private final int mode;
    private final int dimension;
    private final int difficulty;
    private final int maxPlayers;
    private final String levelType;
    private final boolean reducedDebugInfo;

}
