package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.RelativeEntityPositionPacket;

import java.io.IOException;

public final class RelativeEntityPositionCodec implements Codec<RelativeEntityPositionPacket> {
    @Override
    public RelativeEntityPositionPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        short deltaX = buf.readShort();
        short deltaY = buf.readShort();
        short deltaZ = buf.readShort();
        boolean onGround = buf.readBoolean();
        return new RelativeEntityPositionPacket(id, deltaX, deltaY, deltaZ, onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getDeltaX());
        buf.writeShort(message.getDeltaY());
        buf.writeShort(message.getDeltaZ());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
