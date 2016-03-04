package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class HeldItemMessage implements Message {

    private final int slot;

}

