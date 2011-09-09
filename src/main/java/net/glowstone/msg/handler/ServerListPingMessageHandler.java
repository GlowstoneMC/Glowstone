package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.KickMessage;
import net.glowstone.msg.ServerListPingMessage;
import net.glowstone.net.Session;

public class ServerListPingMessageHandler extends MessageHandler<ServerListPingMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, ServerListPingMessage message) {
        session.send(new KickMessage(session.getServer().getMOTD() + "\u00A7" + session.getServer().getOnlinePlayers().length
                + "\u00A7" + session.getServer().getMaxPlayers()));
    }
}
