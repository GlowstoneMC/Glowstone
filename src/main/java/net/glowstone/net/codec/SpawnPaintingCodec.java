package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.SpawnPaintingMessage;
import net.glowstone.util.ChannelBufferUtils;

public final class SpawnPaintingCodec extends MessageCodec<SpawnPaintingMessage> {

	public SpawnPaintingCodec() {
		super(SpawnPaintingMessage.class, 0x19);
	}

	@Override
	public SpawnPaintingMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		String title = ChannelBufferUtils.readString(buffer);
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		int type = buffer.readInt();
		return new SpawnPaintingMessage(id, title, x, y, z, type);
	}

	@Override
	public ChannelBuffer encode(SpawnPaintingMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getId());
		ChannelBufferUtils.writeString(buffer, message.getTitle());
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeInt(message.getType());
		return buffer;
	}

}
