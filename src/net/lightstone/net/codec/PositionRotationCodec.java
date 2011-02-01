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

import net.lightstone.msg.PositionRotationMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class PositionRotationCodec extends MessageCodec<PositionRotationMessage> {

	public PositionRotationCodec() {
		super(PositionRotationMessage.class, 0x0D);
	}

	@Override
	public PositionRotationMessage decode(ChannelBuffer buffer) throws IOException {
		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double stance = buffer.readDouble();
		double z = buffer.readDouble();
		float rotation = buffer.readFloat();
		float pitch = buffer.readFloat();
		boolean onGround = buffer.readByte() == 1;
		return new PositionRotationMessage(x, y, z, stance, rotation, pitch, onGround);
	}

	@Override
	public ChannelBuffer encode(PositionRotationMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(41);
		buffer.writeDouble(message.getX());
		buffer.writeDouble(message.getY());
		buffer.writeDouble(message.getStance());
		buffer.writeDouble(message.getZ());
		buffer.writeFloat(message.getRotation());
		buffer.writeFloat(message.getPitch());
		buffer.writeByte(message.isOnGround() ? 1 : 0);
		return buffer;
	}

}
