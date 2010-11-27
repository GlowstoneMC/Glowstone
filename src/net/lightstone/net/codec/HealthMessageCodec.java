package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.HealthMessage;

public final class HealthMessageCodec extends MessageCodec<HealthMessage> {

	public HealthMessageCodec() {
		super(HealthMessage.class, 0x08);
	}

	@Override
	public HealthMessage decode(ChannelBuffer buffer) throws IOException {
		int health = buffer.readUnsignedByte();
		return new HealthMessage(health);
	}

	@Override
	public ChannelBuffer encode(HealthMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(message.getHealth());
		return buffer;
	}

}
