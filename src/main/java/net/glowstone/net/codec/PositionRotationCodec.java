package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.PositionRotationMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class PositionRotationCodec extends MessageCodec<PositionRotationMessage> {

    public PositionRotationCodec() {
        super(PositionRotationMessage.class, 0x0D);
    }

    @Override
    public PositionRotationMessage decode(ChannelBuffer buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double stance = buffer.readDouble();
        double z = buffer.readDouble();
        float rotation = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean onGround = buffer.readByte() == 1;
        return new PositionRotationMessage(x, y, z, stance, rotation, pitch, onGround);
    }

    @Override
    public ChannelBuffer encode(PositionRotationMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(41);
        buffer.writeDouble(message.getX());
        buffer.writeDouble(message.getY());
        buffer.writeDouble(message.getStance());
        buffer.writeDouble(message.getZ());
        buffer.writeFloat(message.getRotation());
        buffer.writeFloat(message.getPitch());
        buffer.writeByte(message.isOnGround() ? 1 : 0);
        return buffer;
    }

}
