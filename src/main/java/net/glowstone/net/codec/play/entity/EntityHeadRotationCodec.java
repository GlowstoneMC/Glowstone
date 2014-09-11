package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;

import java.io.IOException;

public final class EntityHeadRotationCodec implements Codec<EntityHeadRotationMessage> {
    @Override
    public EntityHeadRotationMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityHeadRotationMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityHeadRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getRotation());
        return buf;
    }
}
