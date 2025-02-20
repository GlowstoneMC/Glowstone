package net.glowstone.util;

import static java.lang.Character.digit;
import static java.nio.charset.StandardCharsets.US_ASCII;

public final class Convert {

    private Convert() {
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hex a string of hexadecimal digits
     * @return {@code hex} as a byte array
     */
    public static byte[] fromHex(String hex) {
        int i = hex.length();
        char[] chars = hex.toCharArray();
        byte[] data = new byte[i >> 1];
        while (--i > -1) {
            int j = i >> 1;
            data[j] = (byte) (digit(chars[i], 16) + (digit(chars[--i], 16) << 4));
        }
        return data;
    }

    private static final byte[] HEX = "0123456789ABCDEF".getBytes(US_ASCII);

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes a byte array
     * @return {@code bytes} as a hexadecimal string
     */
    public static String fromBytes(byte[] bytes) {
        int i = bytes.length;
        int j = i << 1;
        byte[] hexChars = new byte[j];
        while (--i > -1) {
            int v = bytes[i] & 0xFF;
            hexChars[--j] = HEX[v & 0x0F];
            hexChars[--j] = HEX[v >>> 4];
        }
        return new String(hexChars, US_ASCII);
    }

}
