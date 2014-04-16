package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.meta.PlayerProperty;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;

import java.io.IOException;
import java.util.List;

public final class SpawnPlayerCodec implements Codec<SpawnPlayerMessage> {
    public SpawnPlayerMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnPlayerMessage");
    }

    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        final List<PlayerProperty> properties = message.getProperties();

        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, message.getUuid().toString());
        ByteBufUtils.writeUTF8(buf, message.getName());

        if (properties == null) {
            ByteBufUtils.writeVarInt(buf, 0);
        } else {
            ByteBufUtils.writeVarInt(buf, properties.size());
            for (PlayerProperty property : properties) {
                ByteBufUtils.writeUTF8(buf, property.getName());
                ByteBufUtils.writeUTF8(buf, property.getValue());
                ByteBufUtils.writeUTF8(buf, property.getSignature());
            }
        }

        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        buf.writeShort(message.getItem());
        GlowBufUtils.writeMetadata(buf, message.getMetadata());
        return buf;
    }
}
