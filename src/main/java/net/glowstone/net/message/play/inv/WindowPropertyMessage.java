package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class WindowPropertyMessage implements Message {

    private final int id, property, value;

}
