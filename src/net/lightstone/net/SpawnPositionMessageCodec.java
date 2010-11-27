package net.lightstone.net;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.SpawnPositionMessage;
import net.lightstone.net.codec.MessageCodec;

public final class SpawnPositionMessageCodec extends MessageCodec<SpawnPositionMessage> {

	public SpawnPositionMessageCodec() {
		super(SpawnPositionMessage.class, 0x06);
	}

	@Override
	public SpawnPositionMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		return new SpawnPositionMessage(x, y, z);
	}

	@Override
	public ChannelBuffer encode(SpawnPositionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(12);
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		return buffer;
	}

}
