package net.glowstone.net.handler.status;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import org.json.simple.JSONObject;

public final class StatusRequestHandler implements MessageHandler<GlowSession, StatusRequestMessage> {

    @Override
    public void handle(GlowSession session, StatusRequestMessage message) {
        System.out.println("handling status request");
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
