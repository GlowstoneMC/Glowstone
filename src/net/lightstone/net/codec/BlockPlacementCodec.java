package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.BlockPlacementMessage;

public final class BlockPlacementCodec extends MessageCodec<BlockPlacementMessage> {

	public BlockPlacementCodec() {
		super(BlockPlacementMessage.class, 0x0F);
	}

	@Override
	public BlockPlacementMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readShort();
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int direction = buffer.readUnsignedByte();
		return new BlockPlacementMessage(id, x, y, z, direction);
	}

	@Override
	public ChannelBuffer encode(BlockPlacementMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(12);
		buffer.writeShort(message.getId());
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getDirection());
		return buffer;
	}

}
