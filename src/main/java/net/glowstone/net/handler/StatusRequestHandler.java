package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.StatusRequestMessage;
import net.glowstone.net.message.StatusResponseMessage;

public class StatusRequestHandler extends MessageHandler<StatusRequestMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, StatusRequestMessage message) {
        // eventually make this do real things

        String json = "{" +
                "\"version\": {\"name\": \"Glowstone_1.7.2\", \"protocol\": 4}," +
                "\"players\": {\"max\": 20, \"online\": 0}," +
                "\"description\": {\"text\": \"Hello world\"}" +
                "}";
        GlowServer.logger.info("Status request, sending: " + json);
        session.send(new StatusResponseMessage(json));
    }
}
