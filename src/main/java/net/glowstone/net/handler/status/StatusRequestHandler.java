package net.glowstone.net.handler.status;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.util.GlowServerIcon;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.json.simple.JSONObject;

import java.net.InetAddress;

public final class StatusRequestHandler implements MessageHandler<GlowSession, StatusRequestMessage> {

    @Override
    @SuppressWarnings("unchecked")
    public void handle(GlowSession session, StatusRequestMessage message) {
        // create and call the event
        GlowServer server = session.getServer();
        int online = server.getOnlinePlayers().size();
        InetAddress address = session.getAddress().getAddress();

        StatusEvent event = new StatusEvent(address, server.getMotd(), online, server.getMaxPlayers());
        event.icon = server.getServerIcon();
        EventFactory.callEvent(event);

        // build the json
        JSONObject json = new JSONObject();

        JSONObject version = new JSONObject();
        version.put("name", "Glowstone " + GlowServer.GAME_VERSION);
        version.put("protocol", GlowServer.PROTOCOL_VERSION);
        json.put("version", version);

        JSONObject players = new JSONObject();
        players.put("max", event.getMaxPlayers());
        players.put("online", online);
        json.put("players", players);

        JSONObject description = new JSONObject();
        description.put("text", event.getMotd());
        json.put("description", description);

        if (event.icon.getData() != null) {
            json.put("favicon", event.icon.getData());
        }

        // send it off
        session.send(new StatusResponseMessage(json));
    }

    private static class StatusEvent extends ServerListPingEvent {

        private GlowServerIcon icon;

        private StatusEvent(InetAddress address, String motd, int numPlayers, int maxPlayers) {
            super(address, motd, numPlayers, maxPlayers);
        }

        @Override
        public void setServerIcon(CachedServerIcon icon) throws IllegalArgumentException, UnsupportedOperationException {
            if (!(icon instanceof GlowServerIcon)) {
                throw new IllegalArgumentException("Icon not provided by this implementation");
            }
            this.icon = (GlowServerIcon) icon;
        }

        // todo: player list iteration handling
    }
}
