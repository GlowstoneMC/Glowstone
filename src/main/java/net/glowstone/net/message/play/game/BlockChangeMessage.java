package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class BlockChangeMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final int type;

    /**
     * Creates a message indicating that a block has changed.
     *
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     * @param type     the new block ID
     * @param metadata the new block data nibble
     */
    public BlockChangeMessage(int x, int y, int z, int type, int metadata) {
        this(x, y, z, type << 4 | metadata & 0xf);
    }

}
