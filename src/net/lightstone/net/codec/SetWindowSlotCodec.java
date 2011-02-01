/*
 * Copyright (c) 2010-2011 Graham Edgecombe.
 *
 * This file is part of Lightstone.
 *
 * Lightstone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Lightstone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightstone.  If not, see <http://www.gnu.org/licenses/>.
 */

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
			int damage = buffer.readUnsignedByte();
			return new SetWindowSlotMessage(id, slot, item, count, damage);
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
			buffer.writeByte(message.getDamage());
		}
		return buffer;
	}

}
