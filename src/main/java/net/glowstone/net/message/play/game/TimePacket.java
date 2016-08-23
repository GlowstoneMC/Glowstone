package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class TimePacket implements Message {

    private final long worldAge, time;

}
