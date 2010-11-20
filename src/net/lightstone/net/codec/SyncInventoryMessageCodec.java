package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.model.Item;
import net.lightstone.msg.SyncInventoryMessage;

public final class SyncInventoryMessageCodec extends MessageCodec<SyncInventoryMessage> {

	public SyncInventoryMessageCodec() {
		super(SyncInventoryMessage.class, 0x05);
	}

	@Override
	public SyncInventoryMessage decode(ChannelBuffer buffer) throws IOException {
		int type = buffer.readInt();
		int slots = buffer.readUnsignedShort();
		Item[] items = new Item[slots];
		
		for (int i = 0; i < slots; i++) {
			int id = buffer.readShort();
			if (id != -1) {
				int count = buffer.readByte();
				int health = buffer.readShort();
				items[i] = new Item(id, count, health);
			} else {
				items[i] = null;
			}
		}
		
		return new SyncInventoryMessage(type, items);
	}

	@Override
	public ChannelBuffer encode(SyncInventoryMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getType());
		buffer.writeShort(message.getSlots());
		
		Item[] items = message.getItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (item != null) {
				buffer.writeShort(item.getId());
				buffer.writeByte(item.getCount());
				buffer.writeShort(item.getHealth());
			} else {
				buffer.writeShort(-1);
			}
		}
		
		return buffer;
	}

}
