package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class JoinGameMessage implements Message {

    private final int id, mode, dimension, difficulty, maxPlayers;
    private final String levelType;
    private final boolean reducedDebugInfo;

}
