package net.glowstone.msg.handler;

import net.glowstone.entity.Player;
import net.glowstone.msg.Message;
import net.glowstone.net.Session;

public abstract class MessageHandler<T extends Message> {

	public abstract void handle(Session session, Player player, T message);

}
