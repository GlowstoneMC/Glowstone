package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerActionMessage;

public final class PlayerActionHandler implements MessageHandler<GlowSession, PlayerActionMessage> {
    @Override
    public void handle(GlowSession session, PlayerActionMessage message) {
        final GlowPlayer player = session.getPlayer();

        switch (message.getAction()) {
            case 0: // crouch
                player.setSneaking(true);
                break;
            case 1: // uncrouch
                player.setSneaking(false);
                break;
            case 2: // leave bed
                // todo
                break;
            case 3: // start sprinting
                player.setSprinting(true);
                break;
            case 4: // stop sprinting
                player.setSprinting(false);
                break;
            default:
                GlowServer.logger.info("Player " + player + " sent unknown PlayerAction: " + message.getAction());
        }
    }
}
