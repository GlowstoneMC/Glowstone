package net.lightstone.util;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;

public final class ChannelBufferUtils {

	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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
