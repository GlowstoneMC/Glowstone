package net.glowstone.net.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.player.PlayerUpdateMessage;
import org.bukkit.Location;

public class PlayerUpdateHandler extends MessageHandler<PlayerUpdateMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, PlayerUpdateMessage message) {
        Location loc = player.getLocation();
        message.update(loc);
        // do stuff with onGround if we need to
        player.setRawLocation(loc);
    }
}
