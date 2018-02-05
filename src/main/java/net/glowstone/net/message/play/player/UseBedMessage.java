package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class UseBedMessage implements Message {

    private final int id;
    private final int x;
    private final int y;
    private final int z;

}
