package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.TeleportConfirmMessage;

public class TeleportConfirmHandler implements MessageHandler<GlowSession, TeleportConfirmMessage> {

    @Override
    public void handle(GlowSession session, TeleportConfirmMessage message) {
        //TODO: Handle this
    }
}
