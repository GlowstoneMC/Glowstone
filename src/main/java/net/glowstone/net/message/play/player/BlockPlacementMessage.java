package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class BlockPlacementMessage implements Message {

    private final int x, y, z, direction, hand, cursorX, cursorY, cursorZ;

}
