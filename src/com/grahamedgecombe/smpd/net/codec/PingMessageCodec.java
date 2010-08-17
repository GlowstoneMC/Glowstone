package com.grahamedgecombe.smpd.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.grahamedgecombe.smpd.msg.PingMessage;

public final class PingMessageCodec extends MessageCodec<PingMessage> {

	private static final PingMessage PING_MESSAGE = new PingMessage();

	public PingMessageCodec() {
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
