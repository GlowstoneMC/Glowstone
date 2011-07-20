package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.GroundMessage;
import net.glowstone.net.Session;

public class GroundMessageHandler extends MessageHandler<GroundMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, GroundMessage message) {
        if (player != null) {
            player.setOnGround(message.isOnGround());
        }
    }
}
