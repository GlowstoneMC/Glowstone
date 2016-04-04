package net.glowstone.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

/*
  * Originally from http://mechanical-sympathy.blogspot.com/2012/07/native-cc-like-performance-for-java.html
  * Modified by http://java-performance.info/various-methods-of-binary-serialization-in-java/ to add support for bytes/bytearrays
  * Further modifications are by the Glowstone project
 */
public class UnsafeMemory {
    private static final Unsafe unsafe;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final long byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
    private static final long intArrayOffset = unsafe.arrayBaseOffset(int[].class);
    private static final long longArrayOffset = unsafe.arrayBaseOffset(long[].class);
    private static final long doubleArrayOffset = unsafe.arrayBaseOffset(double[].class);

    private static final int SIZE_OF_BOOLEAN = 1;
    private static final int SIZE_OF_BYTE = 1;
    private static final int SIZE_OF_SHORT = 2;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_LONG = 8;
    private static final int SIZE_OF_DOUBLE = 8;

    private int pos = 0;
    private  byte[] buffer;

    public UnsafeMemory(final byte[] buffer) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }
        this.buffer = buffer;
    }

    public void reset() {
        this.pos = 0;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getLength() {
        return pos;
    }

    private void encPos(long enc) {
        if (pos + enc >= buffer.length) {
            buffer = Arrays.copyOf(buffer, buffer.length + 4096);
        }
    }

    private void incPos(long increment) {
        if (pos + increment < buffer.length) {
            pos += increment;
        } else {
            buffer = Arrays.copyOf(buffer, buffer.length + 4096);
            pos += increment;
        }
    }

    ////////////////////////// Boolean //////////////////////////////

    public void putBoolean(final boolean value) {
        encPos(SIZE_OF_BOOLEAN);
        unsafe.putBoolean(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_BOOLEAN);
    }

    public boolean getBoolean() {
        encPos(SIZE_OF_BOOLEAN);
        boolean value = unsafe.getBoolean(buffer, byteArrayOffset + pos);
        incPos(SIZE_OF_BOOLEAN);

        return value;
    }

    ////////////////////////// Byte ////////////////////////////////

    public void putByte(final byte value) {
        //GlowServer.logger.info("writing byte at " + pos);
        encPos(SIZE_OF_BYTE);
        unsafe.putByte(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_BYTE);
        //GlowServer.logger.info("wrote byte to " + pos);
    }

    public byte getByte() {
        encPos(SIZE_OF_BYTE);
        byte value = unsafe.getByte(buffer, byteArrayOffset + pos);
        incPos(SIZE_OF_BYTE);
        return value;
    }

    public void putByteArray(final byte[] values) {
        putInt(values.length);

        long bytesToCopy = values.length;
        encPos(bytesToCopy);
        unsafe.copyMemory(values, byteArrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
        incPos(bytesToCopy);
    }

    public byte[] getByteArray() {
        int arraySize = getInt();
        byte[] values = new byte[arraySize];

        long bytesToCopy = values.length;
        encPos(bytesToCopy);
        unsafe.copyMemory(buffer, byteArrayOffset + pos, values, byteArrayOffset, bytesToCopy);
        incPos(bytesToCopy);

        return values;
    }

    /////////////////////////// Int ////////////////////////////////


    public void putInt(final int value) {
        encPos(SIZE_OF_INT);
        unsafe.putInt(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_INT);
    }

    public int getInt() {
        encPos(SIZE_OF_INT);
        int value = unsafe.getInt(buffer, byteArrayOffset + pos);
        incPos(SIZE_OF_INT);
        return value;
    }

    public void putIntArray(final int[] values) {
        putInt(values.length);

        long bytesToCopy = values.length << 2;
        encPos(bytesToCopy);
        unsafe.copyMemory(values, intArrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
        incPos(bytesToCopy);
    }

    //////////////// Short ///////////////////////////////////////////

    public void putShort(final short value) {
        encPos(SIZE_OF_SHORT);
        unsafe.putShort(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_SHORT);
    }

    public short getShort() {
        encPos(SIZE_OF_LONG);
        short value = unsafe.getShort(buffer, byteArrayOffset + pos);
        incPos(SIZE_OF_LONG);
        return value;
    }

    //////////////// Long ///////////////////////////////////////////

    public void putLong(final long value) {
        encPos(SIZE_OF_LONG);
        unsafe.putLong(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_LONG);
    }

    public long getLong() {
        encPos(SIZE_OF_LONG);
        long value = unsafe.getLong(buffer, byteArrayOffset + pos);
        incPos(SIZE_OF_LONG);
        return value;
    }

    public void putLongArray(final long[] values) {
        putInt(values.length);

        long bytesToCopy = values.length << 3;
        encPos(bytesToCopy);
        unsafe.copyMemory(values, longArrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
        incPos(bytesToCopy);
    }

    public long[] getLongArray() {
        int arraySize = getInt();
        long[] values = new long[arraySize];

        long bytesToCopy = values.length << 3;
        encPos(bytesToCopy);
        unsafe.copyMemory(buffer, byteArrayOffset + pos, values, longArrayOffset, bytesToCopy);
        incPos(bytesToCopy);

        return values;
    }

    //////////////// Double ///////////////////////////////////////////

    public void putDouble(final double value) {
        encPos(SIZE_OF_DOUBLE);
        unsafe.putDouble(buffer, byteArrayOffset + pos, value);
        incPos(SIZE_OF_DOUBLE);
    }

    public void putDoubleArray(final double[] values) {
        putInt(values.length);

        long bytesToCopy = values.length << 3;
        encPos(bytesToCopy);
        unsafe.copyMemory(values, doubleArrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
        incPos(bytesToCopy);
    }

    public double[] getDoubleArray() {
        int arraySize = getInt();
        double[] values = new double[arraySize];

        long bytesToCopy = values.length << 3;
        encPos(bytesToCopy);
        unsafe.copyMemory(buffer, byteArrayOffset + pos, values, doubleArrayOffset, bytesToCopy);
        incPos(bytesToCopy);

        return values;
    }
}
