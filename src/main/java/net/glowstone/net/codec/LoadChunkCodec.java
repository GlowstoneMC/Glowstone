package net.glowstone.net.codec;

import net.glowstone.msg.LoadChunkMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class LoadChunkCodec extends MessageCodec<LoadChunkMessage> {

	public LoadChunkCodec() {
		super(LoadChunkMessage.class, 0x32);
	}

	@Override
	public LoadChunkMessage decode(ChannelBuffer buffer) {
		int x = buffer.readInt();
		int z = buffer.readInt();
		boolean loaded = buffer.readByte() == 1;
		return new LoadChunkMessage(x, z, loaded);
	}

	@Override
	public ChannelBuffer encode(LoadChunkMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(9);
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.isLoaded() ? 1 : 0);
		return buffer;
	}

}
