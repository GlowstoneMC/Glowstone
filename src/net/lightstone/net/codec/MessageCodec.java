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

import net.lightstone.msg.Message;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class MessageCodec<T extends Message> {

	private final Class<T> clazz;
	private final int opcode;

	public MessageCodec(Class<T> clazz, int opcode) {
		this.clazz = clazz;
		this.opcode = opcode;
	}

	public final Class<T> getType() {
		return clazz;
	}

	public final int getOpcode() {
		return opcode;
	}

	public abstract ChannelBuffer encode(T message) throws IOException;

	public abstract T decode(ChannelBuffer buffer) throws IOException;

}
