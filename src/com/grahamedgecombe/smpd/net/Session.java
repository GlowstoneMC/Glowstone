package com.grahamedgecombe.smpd.net;

import org.jboss.netty.channel.Channel;

import com.grahamedgecombe.smpd.msg.KickMessage;
import com.grahamedgecombe.smpd.msg.Message;
import com.grahamedgecombe.smpd.msg.handler.MessageHandler;
import com.grahamedgecombe.smpd.msg.handler.HandlerLookupService;

public final class Session {

	private final Channel channel;

	public Session(Channel channel) {
		this.channel = channel;
		init();
	}

	private void init() {
		send(new KickMessage("Hello, World!"));
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> void messageReceived(T message) {
		MessageHandler<T> handler = (MessageHandler<T>) HandlerLookupService.find(message.getClass());
		handler.handle(this, message);
	}

	public void send(Message message) {
		channel.write(message);
	}

	void dispose() {
		if (channel.isOpen()) {
			channel.close();
		}
	}

}
