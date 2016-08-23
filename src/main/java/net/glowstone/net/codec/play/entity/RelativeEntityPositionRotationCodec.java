package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationPacket;

import java.io.IOException;

public final class RelativeEntityPositionRotationCodec implements Codec<RelativeEntityPositionRotationPacket> {
    @Override
    public RelativeEntityPositionRotationPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        short x = buf.readShort();
        short y = buf.readShort();
        short z = buf.readShort();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        boolean ground = buf.readBoolean();
        return new RelativeEntityPositionRotationPacket(id, x, y, z, rotation, pitch, ground);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionRotationPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getDeltaX());
        buf.writeShort(message.getDeltaY());
        buf.writeShort(message.getDeltaZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
