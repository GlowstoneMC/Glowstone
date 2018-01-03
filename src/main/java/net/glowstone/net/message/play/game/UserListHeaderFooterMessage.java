package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;

@Data
public final class UserListHeaderFooterMessage implements Message {

    private final TextMessage header;
    private final TextMessage footer;

}
