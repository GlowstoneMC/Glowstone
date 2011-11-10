package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.RespawnMessage;
import net.glowstone.net.Session;

public class RespawnMessageHandler extends MessageHandler<RespawnMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, RespawnMessage message) {
        player.setHealth(20);
        player.teleport(player.getWorld().getSpawnLocation());
    }
}
