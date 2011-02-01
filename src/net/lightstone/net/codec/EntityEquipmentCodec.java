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

import net.lightstone.msg.EntityEquipmentMessage;

public final class EntityEquipmentCodec extends MessageCodec<EntityEquipmentMessage> {

	public EntityEquipmentCodec() {
		super(EntityEquipmentMessage.class, 0x05);
	}

	@Override
	public EntityEquipmentMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int slot = buffer.readUnsignedShort();
		int item = buffer.readUnsignedShort();
		int damage = buffer.readUnsignedByte();
		return new EntityEquipmentMessage(id, slot, item, damage);
	}

	@Override
	public ChannelBuffer encode(EntityEquipmentMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(8);
		buffer.writeInt(message.getId());
		buffer.writeShort(message.getSlot());
		buffer.writeShort(message.getItem());
		buffer.writeShort(message.getDamage());
		return buffer;
	}

}
