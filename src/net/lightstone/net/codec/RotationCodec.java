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

import net.lightstone.msg.RotationMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class RotationCodec extends MessageCodec<RotationMessage> {

	public RotationCodec() {
		super(RotationMessage.class, 0x0C);
	}

	@Override
	public RotationMessage decode(ChannelBuffer buffer) throws IOException {
		float rotation = buffer.readFloat();
		float pitch = buffer.readFloat();
		boolean onGround = buffer.readByte() == 1;
		return new RotationMessage(rotation, pitch, onGround);
	}

	@Override
	public ChannelBuffer encode(RotationMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(9);
		buffer.writeFloat(message.getRotation());
		buffer.writeFloat(message.getPitch());
		buffer.writeByte(message.isOnGround() ? 1 : 0);
		return buffer;
	}

}
