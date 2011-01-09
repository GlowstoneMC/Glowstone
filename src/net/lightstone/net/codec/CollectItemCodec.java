package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.CollectItemMessage;

public class CollectItemCodec extends MessageCodec<CollectItemMessage> {

	public CollectItemCodec() {
		super(CollectItemMessage.class, 0x16);
	}

	@Override
	public CollectItemMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int collector = buffer.readInt();
		return new CollectItemMessage(id, collector);
	}

	@Override
	public ChannelBuffer encode(CollectItemMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(8);
		buffer.writeInt(message.getId());
		buffer.writeInt(message.getCollector());
		return buffer;
	}

}
