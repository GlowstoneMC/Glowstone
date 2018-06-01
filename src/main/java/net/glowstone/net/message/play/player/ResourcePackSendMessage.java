package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ResourcePackSendMessage implements Message {

    private final String url;
    private final String hash;
}
