package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

import java.util.List;

@Data
public final class MultiBlockChangeMessage implements Message {

    private final int chunkX, chunkZ;
    private final List<BlockChangeMessage> records;

}
