package net.glowstone.util.nbt;

public class NBTReadLimiter {

    public static final NBTReadLimiter UNLIMITED = new NBTReadLimiter(0L) {

        @Override
        public void read(int length) {
        }
    };

    private final long limit;
    private long read;

    public NBTReadLimiter(long limit) {
        this.limit = limit;
    }

    public void read(int length) {
        read += length;
        if (read > limit) {
            throw new IllegalStateException("Read more than " + limit + " bytes from NBT tag");
        }
    }
}
