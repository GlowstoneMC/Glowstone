package net.glowstone.msg.handler;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.KickMessage;
import net.glowstone.msg.ServerListPingMessage;
import net.glowstone.net.Session;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingMessageHandler extends MessageHandler<ServerListPingMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, ServerListPingMessage message) {
        ServerListPingEvent event = EventFactory.onServerListPing(
                session.getAddress().getAddress(),
                session.getServer().getMotd(),
                session.getServer().getOnlinePlayers().length,
                session.getServer().getMaxPlayers());
        String text = event.getMotd() + "\u00A7" + event.getNumPlayers();
        text += "\u00A7" + event.getMaxPlayers();
        session.send(new KickMessage(text));
    }
    
}
