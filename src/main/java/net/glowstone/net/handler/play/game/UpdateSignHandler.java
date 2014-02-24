package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateSignMessage;

public final class UpdateSignHandler implements MessageHandler<GlowSession, UpdateSignMessage> {
    public void handle(GlowSession session, UpdateSignMessage message) {
        // todo
        GlowServer.logger.info(session + ": " + message);
    }
}
