package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.PositionRotationPacket;

import java.io.IOException;

public final class PositionRotationCodec implements Codec<PositionRotationPacket> {
    @Override
    public PositionRotationPacket decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float rotation = buffer.readFloat();
        float pitch = buffer.readFloat();
        int flags = buffer.readUnsignedByte();
        int teleportID = ByteBufUtils.readVarInt(buffer);
        return new PositionRotationPacket(x, y, z, rotation, pitch, flags, teleportID);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PositionRotationPacket message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getRotation());
        buf.writeFloat(message.getPitch());
        buf.writeByte(message.getFlags());
        ByteBufUtils.writeVarInt(buf, message.getTeleportID());
        return buf;
    }
}
