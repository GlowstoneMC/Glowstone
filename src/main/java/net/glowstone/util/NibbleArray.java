package net.glowstone.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import lombok.Getter;

/**
 * An array of nibbles (4-bit values) stored efficiently as a byte array of half the size.
 *
 * <p>The even indices are stored in the least significant nibble and the odd indices in the most
 * significant bits. For example, [1 5 8 15] is stored as [0x51 0xf8].
 */
public final class NibbleArray {

    /**
     * Get the raw bytes of this nibble array. Modifying the returned array will modify the internal
     * representation of this nibble array.
     *
     * @return The raw bytes.
     */
    @Getter
    private final byte[] rawData;

    /**
     * Construct a new NibbleArray with the given size in nibbles.
     *
     * @param size The number of nibbles in the array.
     * @throws IllegalArgumentException If size is not positive and even.
     */
    public NibbleArray(int size) {
        this(size, (byte) 0);
    }

    /**
     * Construct a new NibbleArray with the given size in nibble, filled with the specified nibble
     * value.
     *
     * @param size The number of nibbles in the array.
     * @param value The value to fill the array with.
     * @throws IllegalArgumentException If size is not positive and even.
     */
    public NibbleArray(int size, byte value) {
        checkArgument(size > 0 && size % 2 == 0, "size must be positive even number, not " + size);
        rawData = new byte[size / 2];
        if (value != 0) {
            fill(value);
        }
    }

    /**
     * Construct a new NibbleArray using the given underlying bytes. No copy is created.
     *
     * @param rawData The raw data to use.
     */
    public NibbleArray(byte... rawData) {
        this.rawData = rawData;
    }

    /**
     * Get the size in nibbles.
     *
     * @return The size in nibbles.
     */
    public int size() {
        return 2 * rawData.length;
    }

    /**
     * Get the size in bytes, one-half the size in nibbles.
     *
     * @return The size in bytes.
     */
    public int byteSize() {
        return rawData.length;
    }

    /**
     * Get the nibble at the given index.
     *
     * @param index The nibble index.
     * @return The value of the nibble at that index.
     */
    public byte get(int index) {
        byte val = rawData[index / 2];
        if (index % 2 == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >> 4);
        }
    }

    /**
     * Set the nibble at the given index to the given value.
     *
     * @param index The nibble index.
     * @param value The new value to store.
     */
    public void set(int index, byte value) {
        value &= 0xf;
        int half = index / 2;
        byte previous = rawData[half];
        if (index % 2 == 0) {
            rawData[half] = (byte) (previous & 0xf0 | value);
        } else {
            rawData[half] = (byte) (previous & 0x0f | value << 4);
        }
    }

    /**
     * Fill the nibble array with the specified value.
     *
     * @param value The value nibble to fill with.
     */
    public void fill(byte value) {
        value &= 0xf;
        Arrays.fill(rawData, (byte) (value << 4 | value));
    }

    /**
     * Copies into the raw bytes of this nibble array from the given source.
     *
     * @param source The array to copy from.
     * @throws IllegalArgumentException If source is not the correct length.
     */
    public void setRawData(byte... source) {
        checkArgument(
                source.length == rawData.length,
                "expected byte array of length " + rawData.length + ", not " + source.length);
        System.arraycopy(source, 0, rawData, 0, source.length);
    }

    /**
     * Take a snapshot of this NibbleArray which will not reflect changes.
     *
     * @return The snapshot NibbleArray.
     */
    public NibbleArray snapshot() {
        return new NibbleArray(rawData.clone());
    }
}
