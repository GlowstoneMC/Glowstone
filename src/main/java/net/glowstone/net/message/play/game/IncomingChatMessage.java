package net.glowstone.net.message.play.game;

import com.flowpowered.network.AsyncableMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class IncomingChatMessage implements AsyncableMessage {

    private String text;

    @Override
    public boolean isAsync() {
        return true;
    }

}
