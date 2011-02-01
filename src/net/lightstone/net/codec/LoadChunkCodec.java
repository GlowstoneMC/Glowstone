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

import net.lightstone.msg.LoadChunkMessage;

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
