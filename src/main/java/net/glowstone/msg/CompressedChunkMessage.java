package net.glowstone.msg;

public final class CompressedChunkMessage extends Message {

    private final int x, z;
    private final int y;
    private final int width, height, depth;
    private final byte[] data;

    public CompressedChunkMessage(int x, int z, int y, int width, int height, int depth, byte[] data) {
        this.x = x;
        this.z = z;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.data = data;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
    return "CompressedChunkMessage{x=" + x + ",y=" + y + ",z=" + z + ",width=" + width + ",height=" + height + ",depth=" + depth + ",data=" + depth + "}";
    }
}
