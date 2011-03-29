package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.Message;
import net.lightstone.net.Session;

public abstract class MessageHandler<T extends Message> {

	public abstract void handle(Session session, Player player, T message);

}
