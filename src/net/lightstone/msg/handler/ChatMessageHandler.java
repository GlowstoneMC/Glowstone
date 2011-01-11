package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.ChatMessage;
import net.lightstone.msg.Message;
import net.lightstone.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {

	@Override
	public void handle(Session session, Player player, ChatMessage message) {
		String text = message.getMessage();
		if (text.length() > 100) {
			session.disconnect("Chat message too long.");
		} else if (text.startsWith("/")) {
			// TODO process command
		} else {
			Message m = new ChatMessage("<" + player.getName() + "> " + text);
			for (Player p : player.getWorld().getPlayers()) {
				p.getSession().send(m);
			}
		}
	}

}
