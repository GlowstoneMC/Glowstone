package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnloadChunkPacket implements Message {

    private final int chunkX, chunkZ;
}
