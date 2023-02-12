package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerSessionMessage;

public final class PlayerSessionHandler implements MessageHandler<GlowSession, PlayerSessionMessage> {
    @Override
    public void handle(GlowSession glowSession, PlayerSessionMessage playerSessionMessage) {
        //TODO: Handle
    }
}
