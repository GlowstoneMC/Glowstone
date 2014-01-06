package net.glowstone.net.message.game;

import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;

public final class SpawnPositionMessage extends Message {

    private final int x, y, z;

    public SpawnPositionMessage(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
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

    @Override
    public String toString() {
        return "SpawnPositionMessage{x=" + x + ",y=" + y + ",z=" + z + "}";
    }
}
