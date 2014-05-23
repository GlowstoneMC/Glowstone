package net.glowstone.util;

import org.apache.commons.lang.Validate;

import java.util.Arrays;

/**
 * An array of nibbles (4-bit values) stored efficiently as a byte array of
 * half the size. The even indices are stored in the least significant nibble
 * and the odd indices in the most significant bits.
 *
 * For example, [1 5 8 15] is stored as [0x51 0xf8].
 */
public final class NibbleArray {

    private final byte[] data;

    /**
     * Construct a new NibbleArray with the given size in nibbles.
     * @param size The number of nibbles in the array.
     * @throws IllegalArgumentException If size is not positive and even.
     */
    public NibbleArray(int size) {
        Validate.isTrue(size > 0 && size % 2 == 0, "size must be positive even number, not " + size);
        data = new byte[size / 2];
    }

    /**
     * Construct a new NibbleArray using the given underlying bytes. No copy
     * is created.
     * @param data The raw data to use.
     */
    public NibbleArray(byte[] data) {
        this.data = data;
    }

    /**
     * Get the size in nibbles.
     * @return The size in nibbles.
     */
    public int size() {
        return 2 * data.length;
    }

    /**
     * Get the size in bytes, one-half the size in nibbles.
     * @return The size in bytes.
     */
    public int byteSize() {
        return data.length;
    }

    /**
     * Get the nibble at the given index.
     * @param index The nibble index.
     * @return The value of the nibble at that index.
     */
    public byte get(int index) {
        byte val = data[index / 2];
        if (index % 2 == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >> 4);
        }
    }

    /**
     * Set the nibble at the given index to the given value.
     * @param index The nibble index.
     * @param value The new value to store.
     */
    public void set(int index, byte value) {
        value &= 0xf;
        int half = index / 2;
        byte previous = data[half];
        if (index % 2 == 0) {
            data[half] = (byte) ((previous & 0xf0) | value);
        } else {
            data[half] = (byte) ((previous & 0x0f) | (value << 4));
        }
    }

    /**
     * Fill the nibble array with the specified value.
     * @param value The value nibble to fill with.
     */
    public void fill(byte value) {
        value &= 0xf;
        Arrays.fill(data, (byte) ((value << 4) | value));
    }

    /**
     * Get the raw bytes of this nibble array. Modifying the returned array
     * will modify the internal representation of this nibble array.
     * @return The raw bytes.
     */
    public byte[] getRawData() {
        return data;
    }

    /**
     * Copies into the raw bytes of this nibble array from the given source.
     * @param source The array to copy from.
     * @throws IllegalArgumentException If source is not the correct length.
     */
    public void setRawData(byte[] source) {
        Validate.isTrue(source.length == data.length, "expected byte array of length " + data.length + ", not " + source.length);
        System.arraycopy(source, 0, data, 0, source.length);
    }

    /**
     * Take a snapshot of this NibbleArray which will not reflect changes.
     * @return The snapshot NibbleArray.
     */
    public NibbleArray snapshot() {
        return new NibbleArray(data.clone());
    }
}
