package net.lightstone.msg.handler;

import net.lightstone.msg.PingMessage;
import net.lightstone.net.Session;

public final class PingMessageHandler extends MessageHandler<PingMessage> {

	@Override
	public void handle(Session session, PingMessage message) {
		session.send(new PingMessage());
	}

}
