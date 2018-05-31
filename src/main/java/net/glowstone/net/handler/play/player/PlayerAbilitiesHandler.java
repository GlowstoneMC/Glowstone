package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public final class PlayerAbilitiesHandler implements
    MessageHandler<GlowSession, PlayerAbilitiesMessage> {

    @Override
    public void handle(GlowSession session, PlayerAbilitiesMessage message) {
        // player sends this when changing whether or not they are currently flying
        // other values should match what we've sent in the past but are ignored here

        GlowPlayer player = session.getPlayer();
        if (!player.getAllowFlight()) {
            return;
        }
        boolean flying = (message.getFlags() & 0x02) != 0;
        PlayerToggleFlightEvent event = EventFactory.getInstance().callEvent(
                new PlayerToggleFlightEvent(player, flying));
        if (!event.isCancelled()) {
            player.setFlying(flying);
        }
    }
}
