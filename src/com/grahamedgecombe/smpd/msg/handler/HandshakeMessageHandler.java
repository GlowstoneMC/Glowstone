package com.grahamedgecombe.smpd.msg.handler;

import com.grahamedgecombe.smpd.msg.HandshakeMessage;
import com.grahamedgecombe.smpd.net.Session;

public final class HandshakeMessageHandler extends MessageHandler<HandshakeMessage> {

	@Override
	public void handle(Session session, HandshakeMessage message) {
		session.send(new HandshakeMessage("-"));
	}

}
