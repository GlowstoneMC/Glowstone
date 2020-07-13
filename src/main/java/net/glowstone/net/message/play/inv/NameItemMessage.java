package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class NameItemMessage implements Message {
    private final String itemName;
}
