package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateStructureBlockMessage;

public final class UpdateStructureBlockHandler implements MessageHandler<GlowSession, UpdateStructureBlockMessage> {
    @Override
    public void handle(GlowSession session, UpdateStructureBlockMessage message) {
        //TODO: handle packet
    }
}
