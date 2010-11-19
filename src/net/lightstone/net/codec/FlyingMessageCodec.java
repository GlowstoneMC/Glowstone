package net.lightstone.net.codec;

import java.io.IOException;

import net.lightstone.msg.FlyingMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class FlyingMessageCodec extends MessageCodec<FlyingMessage> {

	public FlyingMessageCodec() {
		super(FlyingMessage.class, 0x0A);
	}

	@Override
	public FlyingMessage decode(ChannelBuffer buffer) throws IOException {
		return new FlyingMessage(buffer.readByte() == 1);
	}

	@Override
	public ChannelBuffer encode(FlyingMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(message.isFlying() ? 1 : 0);
		return buffer;
	}

}
