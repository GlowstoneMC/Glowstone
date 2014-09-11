package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityStatusMessage;

import java.io.IOException;

public final class EntityStatusCodec implements Codec<EntityStatusMessage> {
    @Override
    public EntityStatusMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityStatusMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityStatusMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getStatus());
        return buf;
    }
}
