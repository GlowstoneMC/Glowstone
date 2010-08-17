package com.grahamedgecombe.smpd.msg.handler;

import com.grahamedgecombe.smpd.msg.PingMessage;
import com.grahamedgecombe.smpd.net.Session;

public final class PingMessageHandler extends MessageHandler<PingMessage> {

	@Override
	public void handle(Session session, PingMessage message) {
		// TODO do something to ensure the channel is kept alive
	}

}
