package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class TimeMessage implements Message {

    private final long worldAge;
    private final long time;

}
