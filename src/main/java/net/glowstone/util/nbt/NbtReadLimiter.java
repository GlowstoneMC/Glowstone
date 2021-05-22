package net.glowstone.util.nbt;

public class NbtReadLimiter {

    public static final NbtReadLimiter UNLIMITED = new NbtReadLimiter(0L) {

        @Override
        public void read(int length) {
        }
    };

    private final long limit;
    private long read;

    public NbtReadLimiter(long limit) {
        this.limit = limit;
    }

    /**
     * Increments the read-length count, and throws an exception if the limit is exceeded.
     *
     * @param length the length to add to the read-length count
     * @throws IllegalStateException if the limit is exceeded
     */
    public void read(int length) {
        read += length;
        if (read > limit) {
            throw new IllegalStateException("Read more than " + limit + " bytes from NBT tag");
        }
    }
}
