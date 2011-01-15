package net.lightstone.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.lightstone.model.Coordinate;

import org.jboss.netty.buffer.ChannelBuffer;

public final class ChannelBufferUtils {

	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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
			case Parameter.TYPE_COORDINATE:
				Coordinate coordinate = ((Parameter<Coordinate>) parameter).getValue();
				buf.writeShort(coordinate.getX());
				buf.writeByte(coordinate.getY());
				buf.writeShort(coordinate.getZ());
				break;
			}
		}

		buf.writeByte(127);
	}

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
			case Parameter.TYPE_COORDINATE:
				int x = buf.readShort();
				int y = buf.readByte();
				int z = buf.readShort();
				Coordinate coordinate = new Coordinate(x, y, z);
				parameters.add(new Parameter<Coordinate>(type, index, coordinate));
				break;
			}
		}

		return parameters;
	}

	public static void writeString(ChannelBuffer buf, String str) {
		byte[] bytes = str.getBytes(CHARSET_UTF8);
		if (bytes.length >= 65536) {
			throw new IllegalArgumentException("Encoded UTF-8 string too long.");
		}

		buf.writeShort(bytes.length);
		buf.writeBytes(bytes);
	}

	public static String readString(ChannelBuffer buf) {
		int length = buf.readUnsignedShort();

		byte[] bytes = new byte[length];
		buf.readBytes(bytes);

		return new String(bytes, CHARSET_UTF8);
	}

	private ChannelBufferUtils() {

	}

}
