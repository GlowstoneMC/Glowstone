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

package net.lightstone.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.lightstone.model.Item;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Contains several {@link ChannelBuffer}-related utility methods.
 * @author Graham Edgecombe
 */
public final class ChannelBufferUtils {

	/**
	 * The UTF-8 character set.
	 */
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * Writes a list of parameters (e.g. mob metadata) to the buffer.
	 * @param buf The buffer.
	 * @param parameters The parameters.
	 */
	@SuppressWarnings("unchecked")
	public static void writeParameters(ChannelBuffer buf, List<Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			int type  = parameter.getType();
			int index = parameter.getIndex();

			buf.writeByte((type << 5) | index);

			switch (type) {
			case Parameter.TYPE_BYTE:
				buf.writeByte(((Parameter<Byte>) parameter).getValue());
				break;
			case Parameter.TYPE_SHORT:
				buf.writeShort(((Parameter<Short>) parameter).getValue());
				break;
			case Parameter.TYPE_INT:
				buf.writeInt(((Parameter<Integer>) parameter).getValue());
				break;
			case Parameter.TYPE_FLOAT:
				buf.writeFloat(((Parameter<Float>) parameter).getValue());
				break;
			case Parameter.TYPE_STRING:
				writeString(buf, ((Parameter<String>) parameter).getValue());
				break;
			case Parameter.TYPE_ITEM:
				Item item = ((Parameter<Item>) parameter).getValue();
				buf.writeShort(item.getId());
				buf.writeByte(item.getCount());
				buf.writeShort(item.getDamage());
				break;
			}
		}

		buf.writeByte(127);
	}

	/**
	 * Reads a list of parameters from the buffer.
	 * @param buf The buffer.
	 * @return The parameters.
	 */
	public static List<Parameter<?>> readParameters(ChannelBuffer buf) {
		List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

		for (int b = buf.readUnsignedByte(); b != 127; ) {
			int type  = (b & 0x0E) >> 5;
			int index = b & 0x1F;

			switch (type) {
			case Parameter.TYPE_BYTE:
				parameters.add(new Parameter<Byte>(type, index, buf.readByte()));
				break;
			case Parameter.TYPE_SHORT:
				parameters.add(new Parameter<Short>(type, index, buf.readShort()));
				break;
			case Parameter.TYPE_INT:
				parameters.add(new Parameter<Integer>(type, index, buf.readInt()));
				break;
			case Parameter.TYPE_FLOAT:
				parameters.add(new Parameter<Float>(type, index, buf.readFloat()));
				break;
			case Parameter.TYPE_STRING:
				parameters.add(new Parameter<String>(type, index, readString(buf)));
				break;
			case Parameter.TYPE_ITEM:
				int id = buf.readShort();
				int count = buf.readByte();
				int damage = buf.readShort();
				Item item = new Item(id, count, damage);
				parameters.add(new Parameter<Item>(type, index, item));
				break;
			}
		}

		return parameters;
	}

	/**
	 * Writes a string to the buffer.
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long
	 * <em>after</em> it is encoded.
	 */
	public static void writeString(ChannelBuffer buf, String str) {
		byte[] bytes = str.getBytes(CHARSET_UTF8);
		if (bytes.length >= 65536) {
			throw new IllegalArgumentException("Encoded UTF-8 string too long.");
		}

		buf.writeShort(bytes.length);
		buf.writeBytes(bytes);
	}

	/**
	 * Reads a string from the buffer.
	 * @param buf The buffer.
	 * @return The string.
	 */
	public static String readString(ChannelBuffer buf) {
		int length = buf.readUnsignedShort();

		byte[] bytes = new byte[length];
		buf.readBytes(bytes);

		return new String(bytes, CHARSET_UTF8);
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private ChannelBufferUtils() {

	}

}
