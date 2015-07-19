package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationMessage;

import java.io.IOException;

public final class RelativeEntityPositionRotationCodec implements Codec<RelativeEntityPositionRotationMessage> {
    @Override
    public RelativeEntityPositionRotationMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int x = buf.readByte();
        int y = buf.readByte();
        int z = buf.readByte();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        boolean ground = buf.readBoolean();
        return new RelativeEntityPositionRotationMessage(id, x, y, z, rotation, pitch, ground);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getDeltaX());
        buf.writeByte(message.getDeltaY());
        buf.writeByte(message.getDeltaZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
