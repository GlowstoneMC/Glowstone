package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.List;

@Data
public final class MultiBlockChangePacket implements Message {

    private final int chunkX, chunkZ;
    private final List<BlockChangePacket> records;

}
