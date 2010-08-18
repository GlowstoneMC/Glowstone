package com.grahamedgecombe.smpd.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.grahamedgecombe.smpd.msg.HandshakeMessage;
import com.grahamedgecombe.smpd.util.ChannelBufferUtils;

public final class HandshakeMessageCodec extends MessageCodec<HandshakeMessage> {

	public HandshakeMessageCodec() {
		super(HandshakeMessage.class, 0x02);
	}

	@Override
	public HandshakeMessage decode(ChannelBuffer buffer) {
		String identifier = ChannelBufferUtils.readString(buffer);
		return new HandshakeMessage(identifier);
	}

	@Override
	public ChannelBuffer encode(HandshakeMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getIdentifier());
		return buffer;
	}

}
