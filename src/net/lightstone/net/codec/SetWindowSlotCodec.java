package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.SetWindowSlotMessage;

public final class SetWindowSlotCodec extends MessageCodec<SetWindowSlotMessage> {

	public SetWindowSlotCodec() {
		super(SetWindowSlotMessage.class, 0x67);
	}

	@Override
	public SetWindowSlotMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int slot = buffer.readUnsignedShort();
		int item = buffer.readUnsignedShort();
		if (item == 0xFFFF) {
			return new SetWindowSlotMessage(id, slot);
		} else {
			int count = buffer.readUnsignedByte();
			int uses = buffer.readUnsignedByte();
			return new SetWindowSlotMessage(id, slot, item, count, uses);
		}
	}

	@Override
	public ChannelBuffer encode(SetWindowSlotMessage message) throws IOException {
		int item = message.getId();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(message.getId());
		buffer.writeShort(message.getSlot());
		buffer.writeShort(message.getItem());
		if (item != -1) {
			buffer.writeByte(message.getCount());
			buffer.writeByte(message.getUses());
		}
		return buffer;
	}

}
