package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;

import java.io.IOException;

public final class EntityTeleportCodec implements Codec<EntityTeleportMessage> {
    @Override
    public EntityTeleportMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityTeleportMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityTeleportMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeBoolean(message.getOnGround());
        return buf;
    }
}
