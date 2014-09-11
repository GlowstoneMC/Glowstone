package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationMessage;

import java.io.IOException;

public final class RelativeEntityPositionRotationCodec implements Codec<RelativeEntityPositionRotationMessage> {
    @Override
    public RelativeEntityPositionRotationMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode RelativeEntityPositionRotationMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getDeltaX());
        buf.writeByte(message.getDeltaY());
        buf.writeByte(message.getDeltaZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.getOnGround());
        return buf;
    }
}
