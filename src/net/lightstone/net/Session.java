package net.lightstone.net;

import net.lightstone.Server;
import net.lightstone.msg.Message;
import net.lightstone.msg.handler.HandlerLookupService;
import net.lightstone.msg.handler.MessageHandler;

import org.jboss.netty.channel.Channel;

public final class Session {

	private final Server server;
	private final Channel channel;

	public Session(Server server, Channel channel) {
		this.server = server;
		this.channel = channel;
	}

	public Server getServer() {
		return server;
	}

	public void send(Message message) {
		channel.write(message);
	}

	@Override
	public String toString() {
		return Session.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
	}

	@SuppressWarnings("unchecked")
	<T extends Message> void messageReceived(T message) {
		MessageHandler<T> handler = (MessageHandler<T>) HandlerLookupService.find(message.getClass());
		if (handler != null) {
			handler.handle(this, message);
		}
	}

	void dispose() {
		if (channel.isOpen()) {
			channel.close();
		}
	}

}
