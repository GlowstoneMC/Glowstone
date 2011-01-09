package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.EntityVelocityMessage;

public final class EntityVelocityCodec extends MessageCodec<EntityVelocityMessage> {

	public EntityVelocityCodec() {
		super(EntityVelocityMessage.class, 0x1C);
	}

	@Override
	public EntityVelocityMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int vx = buffer.readUnsignedShort();
		int vy = buffer.readUnsignedShort();
		int vz = buffer.readUnsignedShort();
		return new EntityVelocityMessage(id, vx, vy, vz);
	}

	@Override
	public ChannelBuffer encode(EntityVelocityMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(10);
		buffer.writeInt(message.getId());
		buffer.writeShort(message.getVelocityX());
		buffer.writeShort(message.getVelocityY());
		buffer.writeShort(message.getVelocityZ());
		return buffer;
	}

}
