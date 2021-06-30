package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class DeclareCommandsMessage implements Message {
    private final int count;
    // TODO: Node for command graph
    private final int rootIndex;
}
