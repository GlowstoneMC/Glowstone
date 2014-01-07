package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public final class BlockChangeMessage extends Message {

    private final int x, y, z, type, metadata;

    public BlockChangeMessage(int x, int y, int z, int type, int metadata) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.metadata = metadata;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeInt(x);
        buf.writeByte(y);
        buf.writeInt(z);
        ChannelBufferUtils.writeVarInt(buf, type);
        buf.writeByte(metadata);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getType() {
        return type;
    }

    public int getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BlockChangeMessage{x=" + x + ",y=" + y +",z=" + z + ",type=" + type + ",metadata=" + metadata + "}";
    }
}
