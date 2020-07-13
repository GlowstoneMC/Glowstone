package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateCommandBlockMessage;

public final class UpdateCommandBlockHandler implements MessageHandler<GlowSession, UpdateCommandBlockMessage> {
    @Override
    public void handle(GlowSession session, UpdateCommandBlockMessage message) {
        //TODO: handle packet
    }
}
