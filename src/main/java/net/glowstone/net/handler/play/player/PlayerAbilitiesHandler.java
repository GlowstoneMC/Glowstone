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
        boolean flying = (message.getFlags() & 0x02) != 0;
        boolean isFlying = player.isFlying();
        if (isFlying != flying) {
            if (!flying || player.getAllowFlight()) {
                PlayerToggleFlightEvent event = EventFactory.getInstance()
                        .callEvent(new PlayerToggleFlightEvent(player, flying));
                player.setFlying(event.isCancelled() ? isFlying : flying);
            }
        }
    }
}
