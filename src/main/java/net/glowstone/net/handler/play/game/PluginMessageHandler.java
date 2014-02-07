package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;

public final class PluginMessageHandler implements MessageHandler<GlowSession, PluginMessage> {
    public void handle(GlowSession session, PluginMessage message) {
        // todo: handle REGISTER, UNREGISTER
        GlowServer.logger.info(session.getPlayer().getName() + " sent plugin message on channel " + message.getChannel());
        session.getServer().getMessenger().dispatchIncomingMessage(session.getPlayer(), message.getChannel(), message.getData());
    }
}
