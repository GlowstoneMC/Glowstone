package net.glowstone.util.nbt;

public class NBTReadLimiter {

    public static final NBTReadLimiter UNLIMITED = new NBTReadLimiter(0L) {

        @Override
        public void read(int length) { }
    };

    private final long limit;
    private long readed = 0;

    public NBTReadLimiter(long limit) {
        this.limit = limit;
    }

    public void read(int length) {
        this.readed += length;
        if (this.readed > this.limit) {
            throw new IllegalStateException("Readed more than " + this.limit + " bytes from NBT tag");
        }
    }
}
