package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import org.bukkit.GameMode;
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
            if (!flyingFlag || canFly) {
                PlayerToggleFlightEvent event = EventFactory.getInstance()
                        .callEvent(new PlayerToggleFlightEvent(player, flyingFlag));
                if (event.isCancelled()) {
                    boolean creative = player.getGameMode() == GameMode.CREATIVE;
                    int flags = (creative ? 8 : 0) | (canFly ? 4 : 0)
                            | (isFlying ? 2 : 0) | (creative ? 1 : 0);
                    // division is conversion from Bukkit to MC units
                    session.send(new PlayerAbilitiesMessage(flags,
                            player.getFlySpeed() / 2F,
                            player.getWalkSpeed() / 2F));
                } else {
                    player.setFlying(flyingFlag);
                }
            }
        }
    }
}
