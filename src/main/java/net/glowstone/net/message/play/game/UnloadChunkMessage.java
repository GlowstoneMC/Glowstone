package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnloadChunkMessage implements Message {

    private final int chunkX;
    private final int chunkZ;
}
