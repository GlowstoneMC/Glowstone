package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class ResourcePackStatusMessage implements Message {

    private final String hash;
    private final int result;

}

