package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class UseBedMessage implements Message {

    private final int id, x, y, z;

}
