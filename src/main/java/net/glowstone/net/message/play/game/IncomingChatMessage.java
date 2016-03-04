package net.glowstone.net.message.play.game;

import com.flowpowered.network.AsyncableMessage;
import lombok.Data;

@Data
public final class IncomingChatMessage implements AsyncableMessage {

    private final String text;

    @Override
    public boolean isAsync() {
        return true;
    }

}
