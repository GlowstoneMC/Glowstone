package net.glowstone.util.nbt;

public class NBTReadLimiter {

    public static final NBTReadLimiter UNLIMITED = new NBTReadLimiter(0L) {

        @Override
        public void read(int length) {
        }
    };

    private final long limit;

    public NBTReadLimiter(long limit) {
        this.limit = limit;
    }

    public void read(int length) {
        long read = 0;
        read += length;
        if (read > this.limit) {
            throw new IllegalStateException("Read more than " + this.limit + " bytes from NBT tag");
        }
    }
}
