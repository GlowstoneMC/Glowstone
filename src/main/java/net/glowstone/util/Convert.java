package net.glowstone.util;

public class Convert {

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hex a string of hexadecimal digits
     * @return {@code hex} as a byte array
     */
    public static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes a byte array
     * @return {@code bytes} as a hexadecimal string
     */
    public static String fromBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder(40);
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
