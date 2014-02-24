package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityEffectMessage;

import java.io.IOException;

public final class EntityEffectCodec implements Codec<EntityEffectMessage> {
    public EntityEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityEffectMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityEffectMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getEffect());
        buf.writeByte(message.getAmplifier());
        buf.writeShort(message.getDuration());
        return buf;
    }
}
