package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;

import java.nio.charset.StandardCharsets;

public final class PluginMessageHandler implements MessageHandler<GlowSession, PluginMessage> {
    public void handle(GlowSession session, PluginMessage message) {

        // todo: handle REGISTER, UNREGISTER
        if (message.getChannel().equals("REGISTER")) {
            // todo
        } else if (message.getChannel().equals("UNREGISTER")) {
            // todo
        } else if (message.getChannel().startsWith("MC|")) {
            // internal Minecraft channels
            handleInternal(session, message.getChannel(), message.getData());
        } else {
            session.getServer().getMessenger().dispatchIncomingMessage(session.getPlayer(), message.getChannel(), message.getData());
        }
    }

    private void handleInternal(GlowSession session, String channel, byte[] data) {
        if (channel.equals("MC|Brand")) {
            // reply with our own brand, this shows up in things like crash reports
            session.send(new PluginMessage("MC|Brand", "Glowstone".getBytes(StandardCharsets.UTF_8)));
            GlowServer.logger.info("Client brand of " + session.getPlayer().getName() + " is: " + new String(data, StandardCharsets.UTF_8));
        } else {
            GlowServer.logger.info("Player " + session.getPlayer().getName() + " used unknown Minecraft channel: " + channel);
        }
    }
}
