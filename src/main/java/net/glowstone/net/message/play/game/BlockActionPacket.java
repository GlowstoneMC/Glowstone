package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class BlockActionPacket implements Message {

    private final int x, y, z, data1, data2, blockType;

}
