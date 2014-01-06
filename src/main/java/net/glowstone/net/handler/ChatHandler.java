package net.glowstone.net.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.game.IncomingChatMessage;

public class ChatHandler extends MessageHandler<IncomingChatMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, IncomingChatMessage message) {
        player.chat(message.getText());
    }
}
