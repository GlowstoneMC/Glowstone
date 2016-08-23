package net.glowstone.net.message;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;

@Data
@RequiredArgsConstructor
public final class KickPacket implements Message {

    private final TextMessage text;

    public KickPacket(String text) {
        this(new TextMessage(text));
    }

}
