package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class BlockChangeMessage implements Message {

    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final int type;

    public BlockChangeMessage(int x, int y, int z, int type, int metadata) {
        this(x, y, z, type << 4 | metadata & 0xf);
    }

}
