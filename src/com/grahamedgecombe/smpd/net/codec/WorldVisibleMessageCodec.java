package com.grahamedgecombe.smpd.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.grahamedgecombe.smpd.msg.WorldVisibleMessage;

public final class WorldVisibleMessageCodec extends MessageCodec<WorldVisibleMessage> {

	public WorldVisibleMessageCodec() {
		super(WorldVisibleMessage.class, 0x0A);
	}

	@Override
	public WorldVisibleMessage decode(ChannelBuffer buffer) throws IOException {
		return new WorldVisibleMessage(buffer.readByte() == 1);
	}

	@Override
	public ChannelBuffer encode(WorldVisibleMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(message.isVisible() ? 1 : 0);
		return buffer;
	}

}
