package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.bukkit.inventory.ItemStack;
import net.glowstone.msg.SetWindowSlotsMessage;

public final class SetWindowSlotsCodec extends MessageCodec<SetWindowSlotsMessage> {

	public SetWindowSlotsCodec() {
		super(SetWindowSlotsMessage.class, 0x68);
	}

	@Override
	public SetWindowSlotsMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int count = buffer.readUnsignedShort();
		ItemStack[] items = new ItemStack[count];
		for (int slot = 0; slot < count; slot++) {
			int item = buffer.readUnsignedShort();
			if (item == 0xFFFF) {
				items[slot] = null;
			} else {
				int itemCount = buffer.readUnsignedByte();
				int damage = buffer.readUnsignedByte();
				items[slot] = new ItemStack(item, itemCount, (short) damage);
			}
		}
		return new SetWindowSlotsMessage(id, items);
	}

	@Override
	public ChannelBuffer encode(SetWindowSlotsMessage message) throws IOException {
		ItemStack[] items = message.getItems();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(message.getId());
		buffer.writeShort(items.length);
		for (int slot = 0; slot < items.length; slot++) {
			ItemStack item = items[slot];
			if (item == null) {
				buffer.writeShort(-1);
			} else {
				buffer.writeShort(item.getTypeId());
				buffer.writeByte(item.getAmount());
				buffer.writeByte(item.getDurability());
			}
		}

		return buffer;
	}

}
