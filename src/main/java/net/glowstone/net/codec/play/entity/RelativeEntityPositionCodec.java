package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;

import java.io.IOException;

public final class RelativeEntityPositionCodec implements Codec<RelativeEntityPositionMessage> {
    @Override
    public RelativeEntityPositionMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode RelativeEntityPositionMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getDeltaX());
        buf.writeByte(message.getDeltaY());
        buf.writeByte(message.getDeltaZ());
        buf.writeBoolean(message.getOnGround());
        return buf;
    }
}
