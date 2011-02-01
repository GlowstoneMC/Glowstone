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

import net.lightstone.msg.BlockPlacementMessage;

public final class BlockPlacementCodec extends MessageCodec<BlockPlacementMessage> {

	public BlockPlacementCodec() {
		super(BlockPlacementMessage.class, 0x0F);
	}

	@Override
	public BlockPlacementMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int direction = buffer.readUnsignedByte();
		int id = buffer.readUnsignedShort();
		if (id == 0xFFFF) {
			return new BlockPlacementMessage(x, y, z, direction);
		} else {
			int count = buffer.readUnsignedByte();
			int damage = buffer.readUnsignedByte();
			return new BlockPlacementMessage(x, y, z, direction, id, count, damage);
		}
	}

	@Override
	public ChannelBuffer encode(BlockPlacementMessage message) throws IOException {
		int id = message.getId();

		ChannelBuffer buffer = ChannelBuffers.buffer(12);
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getDirection());
		buffer.writeShort(id);
		if (id != -1) {
			buffer.writeByte(message.getCount());
			buffer.writeByte(message.getDamage());
		}
		return buffer;
	}

}
