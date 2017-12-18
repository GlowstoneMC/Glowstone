package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class BlockActionMessage implements Message {

    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final int data1;
    private final int data2;
    private final int blockType;

}
