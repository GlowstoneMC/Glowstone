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
