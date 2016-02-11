package net.glowstone.util;

public final class VariableValueArray {

    private final long[] backing;
    private final int capacity;
    private final int bitsPerValue;
    private final long valueMask;

    public VariableValueArray(int bitsPerValue, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException(String.format("capacity (%s) must not be negative", capacity));
        }
        if (bitsPerValue < 1) {
            throw new IllegalArgumentException(String.format("bitsPerValue (%s) must not be smaller then 1", bitsPerValue));
        }
        if (bitsPerValue > 64) {
            throw new IllegalArgumentException(String.format("bitsPerValue (%s) must not be greater then 64", bitsPerValue));
        }
        this.backing = new long[(int) Math.ceil((bitsPerValue * capacity) / 64.0)];
        this.bitsPerValue = bitsPerValue;
        this.valueMask = (1L << bitsPerValue) - 1L;
        this.capacity = capacity;
    }

    public long[] getBacking() {
        return this.backing;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getBitsPerValue() {
        return this.bitsPerValue;
    }

    public int get(int index) {
        this.checkIndex(index);

        index *= this.bitsPerValue;
        int i0 = index >> 6;
        int i1 = index & 0x3f;

        long value = this.backing[i0] >>> i1;
        int i2 = i1 + this.bitsPerValue;
        // The value is divided over two long values
        if (i2 > 64) {
            value |= this.backing[++i0] << (64 - i1);
        }

        return (int) (value & this.valueMask);
    }

    public void set(int index, int value) {
        this.checkIndex(index);

        if (value < 0) {
            throw new IllegalArgumentException(String.format("value (%s) must not be negative", value));
        }
        if (value > this.valueMask) {
            throw new IllegalArgumentException(String.format("value (%s) must not be greater then %s", value, this.valueMask));
        }

        index *= this.bitsPerValue;
        int i0 = index >> 6;
        int i1 = index & 0x3f;

        this.backing[i0] = (this.backing[i0] & ~(this.valueMask << i1)) | (value & this.valueMask) << i1;
        int i2 = i1 + this.bitsPerValue;
        // The value is divided over two long values
        if (i2 > 64) {
            i0++;
            this.backing[i0] = this.backing[i0] & ~((1L << (i2 - 64)) - 1L) | value >> (64 - i1);
        }
    }

    private void checkIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.format("index (%s) must not be negative", index));
        }
        if (index >= this.capacity) {
            throw new IndexOutOfBoundsException(String.format("index (%s) must not be greater then the capacity (%s)", index, this.capacity));
        }
    }
}
