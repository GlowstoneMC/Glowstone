package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;

public class VehicleMoveCodec implements Codec<VehicleMoveMessage> {

    @Override
    public VehicleMoveMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        return new VehicleMoveMessage(x, y, z, yaw, pitch);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, VehicleMoveMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        return buf;
    }
}
