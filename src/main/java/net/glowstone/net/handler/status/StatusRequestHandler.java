package net.glowstone.net.handler.status;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.network.StatusClient;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.flowpowered.network.MessageHandler;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.GlowStatusClient;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.util.UuidUtils;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class StatusRequestHandler implements
    MessageHandler<GlowSession, StatusRequestMessage> {

    private static final UUID BLANK_UUID = new UUID(0, 0);

    private static void choosePlayerSample(GlowServer server, PaperServerListPingEvent event) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Player> players = new ArrayList<>(server.getOnlinePlayers());
        int sampleCount = server.getPlayerSampleCount();
        if (players.size() <= sampleCount) {
            sampleCount = players.size();
        } else {
            // Send a random subset of players (modified Fisher-Yates shuffle)
            for (int i = 0; i < sampleCount; i++) {
                Collections.swap(players, i, random.nextInt(i, players.size()));
            }
        }

        // Add selected players to the event
        for (int i = 0; i < sampleCount; i++) {
            event.getPlayerSample().add(players.get(i).getPlayerProfile());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(GlowSession session, StatusRequestMessage message) {
        // create and call the event
        GlowServer server = session.getServer();
        StatusEvent event = new StatusEvent(
                new GlowStatusClient(session), server.getMotd(),
                server.getOnlinePlayers().size(), server.getMaxPlayers(),
                GlowServer.GAME_VERSION, GlowServer.PROTOCOL_VERSION,
                server.getServerIcon());

        event.serverType = server.getServerType();
        event.clientModsAllowed = server.getAllowClientMods();

        choosePlayerSample(server, event);

        EventFactory.getInstance().callEvent(event);

        // Disconnect immediately if event is cancelled
        if (event.isCancelled()) {
            session.getChannel().close();
            return;
        }

        // build the json
        JSONObject json = new JSONObject();

        JSONObject version = new JSONObject();
        version.put("name", event.getVersion());
        version.put("protocol", event.getProtocolVersion());
        json.put("version", version);

        if (!event.shouldHidePlayers()) {
            JSONObject players = new JSONObject();
            players.put("max", event.getMaxPlayers());
            players.put("online", event.getNumPlayers());

            JSONArray playersSample = new JSONArray();
            for (PlayerProfile profile : event.getPlayerSample()) {
                JSONObject p = new JSONObject();
                p.put("name", Strings.nullToEmpty(profile.getName()));
                p.put("id",
                        UuidUtils.toString(MoreObjects.firstNonNull(profile.getId(), BLANK_UUID)));
                playersSample.add(p);
            }
            players.put("sample", playersSample);

            json.put("players", players);
        }

        JSONObject description = new JSONObject();
        description.put("text", event.getMotd());
        json.put("description", description);

        if (event.getServerIcon() != null) {
            json.put("favicon", event.getServerIcon().getData());
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

    private static class StatusEvent extends PaperServerListPingEvent {

        private String serverType; // VANILLA, BUKKIT, or FML
        private boolean clientModsAllowed;

        private StatusEvent(@Nonnull StatusClient client, String motd, int numPlayers,
                int maxPlayers, @Nonnull String version, int protocolVersion,
                @Nullable CachedServerIcon favicon) {
            super(client, motd, numPlayers, maxPlayers, version, protocolVersion, favicon);
        }

    }
}
