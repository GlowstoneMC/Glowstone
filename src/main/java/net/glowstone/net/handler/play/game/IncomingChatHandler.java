package net.glowstone.net.handler.play.game;

import net.glowstone.net.GlowSession;
import net.glowstone.net.api.EventMessageHandler;
import net.glowstone.net.message.play.game.InboundChatPacket;

public final class IncomingChatHandler extends EventMessageHandler<InboundChatPacket> {

    @Override
    public void handle(GlowSession session, InboundChatPacket message) {
        super.handle(session, message);
        session.getPlayer().chat(message.getText(), message.isAsync());
    }
}
