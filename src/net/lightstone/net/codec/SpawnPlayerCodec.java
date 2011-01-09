package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.SpawnPlayerMessage;
import net.lightstone.util.ChannelBufferUtils;

public final class SpawnPlayerCodec extends MessageCodec<SpawnPlayerMessage> {

	public SpawnPlayerCodec() {
		super(SpawnPlayerMessage.class, 0x14);
	}

	@Override
	public SpawnPlayerMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		String name = ChannelBufferUtils.readString(buffer);
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		int rotation = buffer.readUnsignedByte();
		int pitch = buffer.readUnsignedByte();
		int item = buffer.readUnsignedShort();
		return new SpawnPlayerMessage(id, name, x, y, z, rotation, pitch, item);
	}

	@Override
	public ChannelBuffer encode(SpawnPlayerMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getId());
		ChannelBufferUtils.writeString(buffer, message.getName());
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getRotation());
		buffer.writeByte(message.getPitch());
		buffer.writeShort(message.getItem());
		return buffer;
	}

}
