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
        boolean flyingFlag = (message.getFlags() & 0x02) != 0;

        // the current flying state
        boolean isFlying = player.isFlying();
        boolean canFly = player.getAllowFlight();
        if (isFlying != flyingFlag) {
            // fire the event if either the player disabled flying,
            // or enabled it and is allowed to fly
            if (!flyingFlag || canFly) {
                PlayerToggleFlightEvent event = EventFactory.getInstance()
                    .callEvent(new PlayerToggleFlightEvent(player, flyingFlag));
                if (event.isCancelled()) {
                    session.getServer().sendPlayerAbilities(player);
                } else {
                    player.setFlying(flyingFlag);
                }
            }
        }
    }
}
