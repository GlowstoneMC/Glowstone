package net.lightstone.net.codec;

import java.io.IOException;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.SpawnMobMessage;
import net.lightstone.util.ChannelBufferUtils;
import net.lightstone.util.Parameter;

public final class SpawnMobCodec extends MessageCodec<SpawnMobMessage> {

	public SpawnMobCodec() {
		super(SpawnMobMessage.class, 0x18);
	}

	@Override
	public SpawnMobMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int type = buffer.readUnsignedByte();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		int rotation = buffer.readUnsignedByte();
		int pitch = buffer.readUnsignedByte();
		List<Parameter<?>> parameters = ChannelBufferUtils.readParameters(buffer);
		return new SpawnMobMessage(id, type, x, y, z, rotation, pitch, parameters);
	}

	@Override
	public ChannelBuffer encode(SpawnMobMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(19);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getType());
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getRotation());
		buffer.writeByte(message.getPitch());
		ChannelBufferUtils.writeParameters(buffer, message.getParameters());
		return buffer;
	}

}
