package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class RespawnMessage implements Message {

    // TODO: Dimension (NBT compound)
    private final String world;
    private final byte[] seedHash;
    private final int mode;
    private final int previousMode;
    private final boolean debug;
    private final boolean flat;
    private final boolean copyMetadata;

}
