package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.EntityTeleportMessage;

public final class EntityTeleportCodec extends MessageCodec<EntityTeleportMessage> {

	public EntityTeleportCodec() {
		super(EntityTeleportMessage.class, 0x22);
	}

	@Override
	public EntityTeleportMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		int rotation = buffer.readUnsignedByte();
		int pitch = buffer.readUnsignedByte();
		return new EntityTeleportMessage(id, x, y, z, rotation, pitch);
	}

	@Override
	public ChannelBuffer encode(EntityTeleportMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(18);
		buffer.writeInt(message.getId());
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getRotation());
		buffer.writeByte(message.getPitch());
		return buffer;
	}

}
