package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerActionMessage;

public final class PlayerActionHandler implements MessageHandler<GlowSession, PlayerActionMessage> {
    public void handle(GlowSession session, PlayerActionMessage message) {
        final GlowPlayer player = session.getPlayer();
        if (message.getId() != player.getEntityId()) {
            // vanilla client doesn't send this, ignore the message for now
            return;
        }

        switch (message.getAction()) {
            case 1: // crouch
                player.setSneaking(true);
                break;
            case 2: // uncrouch
                player.setSneaking(false);
                break;
            case 3: // leave bed
                // todo
                break;
            case 4: // start sprinting
                player.setSprinting(true);
                break;
            case 5: // stop sprinting
                player.setSprinting(false);
                break;
            default:
                GlowServer.logger.info("Player " + player + " sent unknown PlayerAction: " + message.getAction());
        }
    }
}
