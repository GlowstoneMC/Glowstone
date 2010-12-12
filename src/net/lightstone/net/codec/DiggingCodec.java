package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.DiggingMessage;

public final class DiggingCodec extends MessageCodec<DiggingMessage> {

	public DiggingCodec() {
		super(DiggingMessage.class, 0x0E);
	}

	@Override
	public DiggingMessage decode(ChannelBuffer buffer) throws IOException {
		int state = buffer.readUnsignedByte();
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int face = buffer.readUnsignedByte();
		return new DiggingMessage(state, x, y, z, face);
	}

	@Override
	public ChannelBuffer encode(DiggingMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(11);
		buffer.writeByte(message.getState());
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeInt(message.getFace());
		return buffer;
	}

}
