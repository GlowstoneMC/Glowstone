package net.glowstone.net.handler.status;

import com.flowpowered.network.MessageHandler;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.util.GlowServerIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class StatusRequestHandler implements
    MessageHandler<GlowSession, StatusRequestMessage> {

    @Override
    @SuppressWarnings("unchecked")
    public void handle(GlowSession session, StatusRequestMessage message) {
        // create and call the event
        GlowServer server = session.getServer();
        int online = server.getOnlinePlayers().size();
        InetAddress address = session.getAddress().getAddress();

        StatusEvent event = new StatusEvent(address, server.getMotd(), online,
            server.getMaxPlayers());
        event.players = new ArrayList<>(server.getOnlinePlayers());
        event.icon = server.getServerIcon();
        event.serverType = server.getServerType();
        event.clientModsAllowed = server.getAllowClientMods();
        EventFactory.getInstance().callEvent(event);

        // build the json
        JSONObject json = new JSONObject();

        JSONObject version = new JSONObject();
        String gameVersion = GlowServer.GAME_VERSION;
        int protocolVersion = GlowServer.PROTOCOL_VERSION;
        version.put("name", gameVersion);
        version.put("protocol", protocolVersion);
        json.put("version", version);

        JSONObject players = new JSONObject();
        players.put("max", event.getMaxPlayers());
        players.put("online", online);

        if (!event.players.isEmpty()) {
            event.players = event.players
                .subList(0, Math.min(event.players.size(), server.getPlayerSampleCount()));
            Collections.shuffle(event.players);
            JSONArray playersSample = new JSONArray();

            for (Player player : event.players) {
                JSONObject p = new JSONObject();
                p.put("name", player.getName());
                p.put("id", player.getUniqueId().toString());
                playersSample.add(p);
            }
            players.put("sample", playersSample);
        }

        json.put("players", players);

        JSONObject description = new JSONObject();
        description.put("text", event.getMotd());
        json.put("description", description);

        if (event.icon.getData() != null) {
            json.put("favicon", event.icon.getData());
        }

        // Mod list must be included but can be empty
        // TODO: support adding GS-ported Forge server-side mods?
        JSONArray modList = new JSONArray();

        JSONObject modinfo = new JSONObject();
        modinfo.put("type", event.serverType);
        modinfo.put("modList", modList);
        if (!event.clientModsAllowed) {
            modinfo.put("clientModsAllowed", false);
        }
        json.put("modinfo", modinfo);

        // send it off
        session.send(new StatusResponseMessage(json));
    }

    private static class StatusEvent extends ServerListPingEvent {

        private GlowServerIcon icon;
        private List<Player> players;
        private String serverType; // VANILLA, BUKKIT, or FML
        private boolean clientModsAllowed;

        private StatusEvent(InetAddress address, String motd, int numPlayers, int maxPlayers) {
            super(address, motd, numPlayers, maxPlayers);
        }

        @Override
        public void setServerIcon(CachedServerIcon icon)
            throws IllegalArgumentException, UnsupportedOperationException {
            if (!(icon instanceof GlowServerIcon)) {
                throw new IllegalArgumentException("Icon not provided by this implementation");
            }
            this.icon = (GlowServerIcon) icon;
        }

        @Override
        public Iterator<Player> iterator() {
            return players.iterator();
        }
    }
}
