package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.EntityActionMessage;

public final class EntityActionCodec extends MessageCodec<EntityActionMessage> {

	public EntityActionCodec() {
		super(EntityActionMessage.class, 0x13);
	}

	@Override
	public EntityActionMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int action = buffer.readUnsignedByte();
		return new EntityActionMessage(id, action);
	}

	@Override
	public ChannelBuffer encode(EntityActionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(5);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getAction());
		return buffer;
	}

}
