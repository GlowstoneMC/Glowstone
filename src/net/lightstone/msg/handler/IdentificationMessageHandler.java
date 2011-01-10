package net.lightstone.msg.handler;

import java.util.logging.Logger;

import net.lightstone.msg.IdentificationMessage;
import net.lightstone.net.Session;

public final class IdentificationMessageHandler extends MessageHandler<IdentificationMessage> {

	private static final Logger logger = Logger.getLogger(IdentificationMessageHandler.class.getName());

	@Override
	public void handle(Session session, IdentificationMessage message) {
		logger.info(session + " identified as: " + message.getName() + " (using protocol " + message.getId() + ")");
		session.send(new IdentificationMessage(0, "", "", 0, 0));
	}

}
