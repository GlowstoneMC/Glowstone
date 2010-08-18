package com.grahamedgecombe.smpd.msg.handler;

import com.grahamedgecombe.smpd.msg.IdentificationMessage;
import com.grahamedgecombe.smpd.net.Session;

public final class IdentificationMessageHandler extends MessageHandler<IdentificationMessage> {

	@Override
	public void handle(Session session, IdentificationMessage message) {
		session.send(new IdentificationMessage(0, "", ""));
		session.init(); // TODO temp!!!
	}

}
