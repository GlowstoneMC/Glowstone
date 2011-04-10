package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.BlockChangeMessage;

public final class BlockChangeCodec extends MessageCodec<BlockChangeMessage> {

	public BlockChangeCodec() {
		super(BlockChangeMessage.class, 0x35);
	}

	@Override
	public BlockChangeMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int type = buffer.readUnsignedByte();
		int metadata = buffer.readUnsignedByte();
		return new BlockChangeMessage(x, y, z, type, metadata);
	}

	@Override
	public ChannelBuffer encode(BlockChangeMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(11);
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getType());
		buffer.writeByte(message.getMetadata());
		return buffer;
	}

}
