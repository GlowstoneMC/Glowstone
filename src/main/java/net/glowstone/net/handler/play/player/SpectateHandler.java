package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.SpectateMessage;

public final class SpectateHandler implements MessageHandler<GlowSession, SpectateMessage> {
    @Override
    public void handle(GlowSession session, SpectateMessage message) {
        GlowServer.logger.info(session + ": " + message);
    }
}
