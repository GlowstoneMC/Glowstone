package net.lightstone.msg.handler;

import java.util.logging.Logger;

import net.lightstone.msg.HandshakeMessage;
import net.lightstone.net.Session;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {

	private static final Logger logger = Logger.getLogger(HandshakeMessageHandler.class.getName());

	@Override
	public void handle(Session session, HandshakeMessage message) {
		logger.info(session + " handshaking: " + message.getIdentifier());
		session.send(new HandshakeMessage("-"));
	}

}
