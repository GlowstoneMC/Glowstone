package net.lightstone.net;

import net.lightstone.msg.ChatMessage;
import net.lightstone.msg.CompressedChunkMessage;
import net.lightstone.msg.GroundMessage;
import net.lightstone.msg.LoadChunkMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.handler.HandlerLookupService;
import net.lightstone.msg.handler.MessageHandler;

import org.jboss.netty.channel.Channel;

public final class Session {

	private final Channel channel;

	public Session(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "[address=" + channel.getRemoteAddress() + "]";
	}

	public void init() {
		send(new ChatMessage("Hello, World!"));

		for (int x = -8; x <= 8; x++) {
			for (int z = -8; z <= 8; z++) {
				byte[] data = new byte[(16 * 16 * 128 * 5) / 2];

				for (int tx = 0; tx < 16; tx++) {
					for (int tz = 0; tz < 16; tz++) {
						for (int ty = 0; ty < 128; ty++) {
							data[((tx * 16) + tz) * 128 + ty] = (byte) (ty > 60 ? 0 : 4);
						}
					}
				}

				for (int tx = 0; tx < 16; tx++) {
					for (int tz = 0; tz < 16; tz++) {
						for (int ty = 0; ty < 128; ty++) {
							data[(16 * 16 * 128) + ((tx * 16) + tz) * 128 + ty] = 0;
						}
					}
				}

				for (int tx = 0; tx < 16; tx++) {
					for (int tz = 0; tz < 16; tz++) {
						for (int ty = 0; ty < 64; ty++) {
							data[(16 * 16 * 128 * 2) + ((tx * 16) + tz) * 64 + ty] = (byte) 0xFF;
						}
					}
				}

				send(new LoadChunkMessage(x, z, true));
				send(new CompressedChunkMessage(x * 16, z * 16, 0, 16, 16, 128, data));
			}
		}

		send(new GroundMessage(true));
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> void messageReceived(T message) {
		MessageHandler<T> handler = (MessageHandler<T>) HandlerLookupService.find(message.getClass());
		if (handler != null) {
			handler.handle(this, message);
		}
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
