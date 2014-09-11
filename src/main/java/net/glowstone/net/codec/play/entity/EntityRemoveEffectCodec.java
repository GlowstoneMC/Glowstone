package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityRemoveEffectMessage;

import java.io.IOException;

public final class EntityRemoveEffectCodec implements Codec<EntityRemoveEffectMessage> {
    @Override
    public EntityRemoveEffectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityRemoveEffectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityRemoveEffectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getEffect());
        return buf;
    }
}
