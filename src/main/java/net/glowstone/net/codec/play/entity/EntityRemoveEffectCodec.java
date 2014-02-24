package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityRemoveEffectMessage;

import java.io.IOException;

public final class EntityRemoveEffectCodec implements Codec<EntityRemoveEffectMessage> {
    public EntityRemoveEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityRemoveEffectMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityRemoveEffectMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getEffect());
        return buf;
    }
}
