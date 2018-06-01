package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class BlockActionMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final int data1;
    private final int data2;
    private final int blockType;

}
