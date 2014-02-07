package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;

import java.io.IOException;

public final class EntityTeleportCodec implements Codec<EntityTeleportMessage> {
    public EntityTeleportMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityTeleportMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityTeleportMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        return buf;
    }
}
