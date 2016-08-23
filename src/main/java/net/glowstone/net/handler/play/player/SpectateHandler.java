package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.SpectatePacket;

public final class SpectateHandler implements MessageHandler<GlowSession, SpectatePacket> {
    @Override
    public void handle(GlowSession session, SpectatePacket message) {
        GlowServer.logger.info(session + ": " + message);
    }
}
