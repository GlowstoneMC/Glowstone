package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.PositionRotationMessage;

public final class PositionRotationCodec implements Codec<PositionRotationMessage> {

    @Override
    public PositionRotationMessage decode(ByteBuf buffer) throws IOException {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float rotation = buffer.readFloat();
        float pitch = buffer.readFloat();
        int flags = buffer.readUnsignedByte();
        int teleportId = ByteBufUtils.readVarInt(buffer);
        return new PositionRotationMessage(x, y, z, rotation, pitch, flags, teleportId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PositionRotationMessage message) throws IOException {
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getRotation());
        buf.writeFloat(message.getPitch());
        buf.writeByte(message.getFlags());
        ByteBufUtils.writeVarInt(buf, message.getTeleportId());
        return buf;
    }
}
