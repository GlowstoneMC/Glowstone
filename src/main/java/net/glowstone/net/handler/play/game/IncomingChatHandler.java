package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.IncomingChatMessage;

public final class IncomingChatHandler implements MessageHandler<GlowSession, IncomingChatMessage> {

    @Override
    public void handle(GlowSession session, IncomingChatMessage message) {
        if (!session.isOnline()) {
            // deny the message if the player hasn't had the login procedure completed
            return;
        }
        if (!message.getText().isEmpty()) {
            session.getPlayer().chat(message.getText(), message.isAsync());
        }
    }
}
