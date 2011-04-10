package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.RelativeEntityPositionMessage;

public final class RelativeEntityPositionCodec extends MessageCodec<RelativeEntityPositionMessage> {

	public RelativeEntityPositionCodec() {
		super(RelativeEntityPositionMessage.class, 0x1F);
	}

	@Override
	public RelativeEntityPositionMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int dx = buffer.readByte();
		int dy = buffer.readByte();
		int dz = buffer.readByte();
		return new RelativeEntityPositionMessage(id, dx, dy, dz);
	}

	@Override
	public ChannelBuffer encode(RelativeEntityPositionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(7);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getDeltaX());
		buffer.writeByte(message.getDeltaY());
		buffer.writeByte(message.getDeltaZ());
		return buffer;
	}

}
