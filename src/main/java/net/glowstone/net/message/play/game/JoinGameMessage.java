package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class JoinGameMessage implements Message {

    private final int id;
    private final boolean hardcore;
    private final int mode;
    private final int previousMode;
    private final String[] worlds;
    // TODO: Dimension codec (NBT compound)
    // TODO: Dimension (NBT compound)
    private final String currentWorld;
    private final byte[] seedHash;
    private final int maxPlayers;
    private final int viewDistance;
    private final boolean reducedDebugInfo;
    private final boolean enableRespawnScreen;
    private final boolean debug;
    private final boolean flat;

}
