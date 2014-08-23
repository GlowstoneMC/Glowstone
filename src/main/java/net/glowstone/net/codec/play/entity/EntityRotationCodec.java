package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityRotationMessage;

import java.io.IOException;

public final class EntityRotationCodec implements Codec<EntityRotationMessage> {
    public EntityRotationMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityRotationMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityRotationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(true); // todo: on ground flag
        return buf;
    }
}
