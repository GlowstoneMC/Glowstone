package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PingPacket implements Message {

    private final int pingId;

}
