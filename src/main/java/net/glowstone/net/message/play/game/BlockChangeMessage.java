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

}
