package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.VehicleMovePacket;

import java.io.IOException;

public class VehicleMoveCodec implements Codec<VehicleMovePacket> {
    @Override
    public VehicleMovePacket decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        return new VehicleMovePacket(x, y, z, yaw, pitch);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, VehicleMovePacket message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        return buf;
    }
}
