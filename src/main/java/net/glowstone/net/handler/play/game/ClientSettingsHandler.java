package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.ClientSettingsMessage;

public final class ClientSettingsHandler implements MessageHandler<GlowSession, ClientSettingsMessage> {
    @Override
    public void handle(GlowSession session, ClientSettingsMessage message) {
        GlowServer.logger.info(session.getPlayer().getName() + ": " + message);
    }
}
