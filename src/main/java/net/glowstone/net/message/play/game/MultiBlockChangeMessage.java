package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;

@Data
public final class MultiBlockChangeMessage implements Message {

    private final int chunkX;
    private final int chunkZ;
    private final List<BlockChangeMessage> records;

}
