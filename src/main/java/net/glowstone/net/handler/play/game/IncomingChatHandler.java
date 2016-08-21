package net.glowstone.net.handler.play.game;

import net.glowstone.net.GlowSession;
import net.glowstone.net.api.EventMessageHandler;
import net.glowstone.net.message.play.game.IncomingChatMessage;

public final class IncomingChatHandler extends EventMessageHandler<IncomingChatMessage> {

    @Override
    public void handle(GlowSession session, IncomingChatMessage message) {
        super.handle(session, message);
        session.getPlayer().chat(message.getText(), message.isAsync());
    }
}
