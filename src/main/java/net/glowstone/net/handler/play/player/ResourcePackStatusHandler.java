package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;

public final class ResourcePackStatusHandler implements MessageHandler<GlowSession, ResourcePackStatusMessage> {
    @Override
    public void handle(GlowSession session, ResourcePackStatusMessage message) {
        GlowServer.logger.info(session + ": " + message);
    }
}
