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
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.EntityMetadataMessage;
import net.lightstone.util.ChannelBufferUtils;
import net.lightstone.util.Parameter;

public final class EntityMetadataCodec extends MessageCodec<EntityMetadataMessage> {

	public EntityMetadataCodec() {
		super(EntityMetadataMessage.class, 0x28);
	}

	@Override
	public EntityMetadataMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		List<Parameter<?>> parameters = ChannelBufferUtils.readParameters(buffer);
		return new EntityMetadataMessage(id, parameters);
	}

	@Override
	public ChannelBuffer encode(EntityMetadataMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getId());
		ChannelBufferUtils.writeParameters(buffer, message.getParameters());
		return buffer;
	}

}
