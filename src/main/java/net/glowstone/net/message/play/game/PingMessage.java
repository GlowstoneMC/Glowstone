package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PingMessage implements Message {

    private final long pingId;

}
