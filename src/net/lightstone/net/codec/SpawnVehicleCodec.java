package net.lightstone.net.codec;

import java.io.IOException;

import net.lightstone.msg.SpawnVehicleMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class SpawnVehicleCodec extends MessageCodec<SpawnVehicleMessage> {

	public SpawnVehicleCodec() {
		super(SpawnVehicleMessage.class, 0x17);
	}

	@Override
	public SpawnVehicleMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int type = buffer.readUnsignedByte();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		return new SpawnVehicleMessage(id, type, x, y, z);
	}

	@Override
	public ChannelBuffer encode(SpawnVehicleMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(17);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getType());
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		return buffer;
	}

}
