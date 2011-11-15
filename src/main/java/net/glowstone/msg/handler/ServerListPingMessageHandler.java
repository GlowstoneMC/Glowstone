package net.glowstone.msg.handler;

import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.KickMessage;
import net.glowstone.msg.ServerListPingMessage;
import net.glowstone.net.Session;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Format: (MOTD)/u00A7(# online)/u00A7(Max Players)
 * /u00A7(Protocol Version (This is added in case someone finds it useful,
 * since it's not used by the vanilla client.))
 */
public class ServerListPingMessageHandler extends MessageHandler<ServerListPingMessage> {

    @Override

    public void handle(Session session, GlowPlayer player, ServerListPingMessage message) {
        ServerListPingEvent event = EventFactory.onServerListPing(
                session.getAddress().getAddress(),
                session.getServer().getMotd(),
                session.getServer().getOnlinePlayers().length,
                session.getServer().getMaxPlayers());
        String text = event.getMotd() + "\u00A7" + event.getNumPlayers();
        text += "\u00A7" + event.getMaxPlayers() + "\u00A7" + GlowServer.PROTOCOL_VERSION;
        session.send(new KickMessage(text));
    }
    
}
