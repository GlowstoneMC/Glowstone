package net.glowstone.msg.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.ChatMessage;
import net.glowstone.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, ChatMessage message) {
		if (player == null)
			return;

		String text = message.getMessage();
        text = text.trim();
        
		if (text.length() > 100) {
			session.disconnect("Chat message too long.");
		} else if (text.startsWith("/")) {
			// TODO: process command
		} else {
            player.getServer().broadcastMessage("<" + player.getName() + "> " + text);
			player.getWorld().broadcastMessage("[" + player.getWorld().getName() + "]<" + player.getName() + "> " + text);
            GlowServer.logger.info("<" + player.getName() + "> " + text);
		}
	}

}
