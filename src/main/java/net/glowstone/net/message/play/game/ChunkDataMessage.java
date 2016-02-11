package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class ChunkDataMessage implements Message {

    private final int x, z;
    private final boolean continuous;
    private final int primaryMask;
    private final byte[] data;

}
