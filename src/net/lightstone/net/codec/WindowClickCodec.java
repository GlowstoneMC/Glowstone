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

import net.lightstone.msg.WindowClickMessage;

public final class WindowClickCodec extends MessageCodec<WindowClickMessage> {

	public WindowClickCodec() {
		super(WindowClickMessage.class, 0x66);
	}

	@Override
	public WindowClickMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int slot = buffer.readUnsignedShort();
		boolean rightClick = buffer.readUnsignedByte() != 0;
		int transaction = buffer.readUnsignedShort();
		int item = buffer.readUnsignedShort();
		if (item == 0xFFFF) {
			return new WindowClickMessage(id, slot, rightClick, transaction);
		} else {
			int count = buffer.readUnsignedByte();
			int damage = buffer.readUnsignedByte();
			return new WindowClickMessage(id, slot, rightClick, transaction, item, count, damage);
		}
	}

	@Override
	public ChannelBuffer encode(WindowClickMessage message) throws IOException {
		int item = message.getItem();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(message.getId());
		buffer.writeShort(message.getSlot());
		buffer.writeByte(message.isRightClick() ? 1 : 0);
		buffer.writeShort(message.getTransaction());
		buffer.writeShort(item);
		if (item != -1) {
			buffer.writeByte(message.getCount());
			buffer.writeByte(message.getDamage());
		}
		return buffer;
	}

}
