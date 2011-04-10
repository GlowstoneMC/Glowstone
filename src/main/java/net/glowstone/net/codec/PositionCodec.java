package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.PositionMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class PositionCodec extends MessageCodec<PositionMessage> {

	public PositionCodec() {
		super(PositionMessage.class, 0x0B);
	}

	@Override
	public PositionMessage decode(ChannelBuffer buffer) throws IOException {
		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double stance = buffer.readDouble();
		double z = buffer.readDouble();
		boolean flying = buffer.readByte() == 1;
		return new PositionMessage(x, y, z, stance, flying);
	}

	@Override
	public ChannelBuffer encode(PositionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(33);
		buffer.writeDouble(message.getX());
		buffer.writeDouble(message.getY());
		buffer.writeDouble(message.getStance());
		buffer.writeDouble(message.getZ());
		buffer.writeByte(message.isOnGround() ? 1 : 0);
		return buffer;
	}

}
