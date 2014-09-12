package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;

import java.io.IOException;

public final class RelativeEntityPositionCodec implements Codec<RelativeEntityPositionMessage> {
    @Override
    public RelativeEntityPositionMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int deltaX = buf.readByte();
        int deltaY = buf.readByte();
        int deltaZ = buf.readByte();
        boolean onGround = buf.readBoolean();
        return new RelativeEntityPositionMessage(id, deltaX, deltaY, deltaZ, onGround);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getDeltaX());
        buf.writeByte(message.getDeltaY());
        buf.writeByte(message.getDeltaZ());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
