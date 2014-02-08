package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;

import java.io.IOException;

public final class RelativeEntityPositionCodec implements Codec<RelativeEntityPositionMessage> {
    public RelativeEntityPositionMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode RelativeEntityPositionMessage");
    }

    public ByteBuf encode(ByteBuf buf, RelativeEntityPositionMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getDeltaX());
        buf.writeByte(message.getDeltaY());
        buf.writeByte(message.getDeltaZ());
        return buf;
    }
}
