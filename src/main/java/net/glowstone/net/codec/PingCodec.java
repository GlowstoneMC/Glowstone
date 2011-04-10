package net.glowstone.net.codec;

import net.glowstone.msg.PingMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class PingCodec extends MessageCodec<PingMessage> {

	private static final PingMessage PING_MESSAGE = new PingMessage();

	public PingCodec() {
		super(PingMessage.class, 0x00);
	}

	@Override
	public PingMessage decode(ChannelBuffer buffer) {
		return PING_MESSAGE;
	}

	@Override
	public ChannelBuffer encode(PingMessage message) {
		return ChannelBuffers.EMPTY_BUFFER;
	}

}
