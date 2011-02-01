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

import net.lightstone.msg.PlayNoteMessage;

public final class PlayNoteCodec extends MessageCodec<PlayNoteMessage> {

	public PlayNoteCodec() {
		super(PlayNoteMessage.class, 0x36);
	}

	@Override
	public PlayNoteMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readUnsignedShort();
		int z = buffer.readInt();
		int instrument = buffer.readUnsignedByte();
		int pitch = buffer.readUnsignedByte();
		return new PlayNoteMessage(x, y, z, instrument, pitch);
	}

	@Override
	public ChannelBuffer encode(PlayNoteMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(12);
		buffer.writeInt(message.getX());
		buffer.writeShort(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getInstrument());
		buffer.writeByte(message.getPitch());
		return buffer;
	}

}
