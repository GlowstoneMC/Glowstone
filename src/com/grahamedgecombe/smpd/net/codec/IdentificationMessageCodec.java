package com.grahamedgecombe.smpd.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.grahamedgecombe.smpd.msg.IdentificationMessage;
import com.grahamedgecombe.smpd.util.ChannelBufferUtils;

public final class IdentificationMessageCodec extends MessageCodec<IdentificationMessage> {

	public IdentificationMessageCodec() {
		super(IdentificationMessage.class, 0x01);
	}

	@Override
	public IdentificationMessage decode(ChannelBuffer buffer) {
		int version = buffer.readInt();
		String name = ChannelBufferUtils.readString(buffer);
		String password = ChannelBufferUtils.readString(buffer);
		return new IdentificationMessage(version, name, password);
	}

	@Override
	public ChannelBuffer encode(IdentificationMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getId());
		ChannelBufferUtils.writeString(buffer, message.getName());
		ChannelBufferUtils.writeString(buffer, message.getMessage());
		return buffer;
	}

}
