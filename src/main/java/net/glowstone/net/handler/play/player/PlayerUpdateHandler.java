package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;
import org.bukkit.Location;

public final class PlayerUpdateHandler implements MessageHandler<GlowSession, PlayerUpdateMessage> {

    @Override
    public void handle(GlowSession session, PlayerUpdateMessage message) {
        Location loc = session.getPlayer().getLocation();
        message.update(loc);
        // do stuff with onGround if we need to
        session.getPlayer().setRawLocation(loc);
    }
}
