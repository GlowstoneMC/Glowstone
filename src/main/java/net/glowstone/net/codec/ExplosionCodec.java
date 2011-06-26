package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.ExplosionMessage;

public final class ExplosionCodec extends MessageCodec<ExplosionMessage> {

    public ExplosionCodec() {
        super(ExplosionMessage.class, 0x3C);
    }

    @Override
    public ExplosionMessage decode(ChannelBuffer buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float radius = buffer.readFloat();
        int records = buffer.readInt();
        byte[] coordinates = new byte[records * 3];
        buffer.readBytes(coordinates);
        return new ExplosionMessage(x, y, z, radius, coordinates);
    }

    @Override
    public ChannelBuffer encode(ExplosionMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeDouble(message.getX());
        buffer.writeDouble(message.getY());
        buffer.writeDouble(message.getZ());
        buffer.writeFloat(message.getRadius());
        buffer.writeInt(message.getRecords());
        buffer.writeBytes(message.getCoordinates());
        return buffer;
    }

}
