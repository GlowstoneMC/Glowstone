package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import org.json.simple.JSONObject;

public class StatusRequestHandler extends MessageHandler<StatusRequestMessage> {
    @Override
    @SuppressWarnings("unchecked")
    public void handle(Session session, GlowPlayer player, StatusRequestMessage message) {
        // eventually make this do real things

        JSONObject json = new JSONObject();

        JSONObject version = new JSONObject();
        version.put("name", "Glowstone_1.7.2");
        version.put("protocol", GlowServer.PROTOCOL_VERSION);
        json.put("version", version);

        JSONObject players = new JSONObject();
        players.put("max", 20);
        players.put("online", 0);
        json.put("players", players);

        JSONObject description = new JSONObject();
        description.put("text", "Hello world");
        json.put("description", description);

        GlowServer.logger.info("Status request, sending: " + json);
        session.send(new StatusResponseMessage(json));
    }
}
