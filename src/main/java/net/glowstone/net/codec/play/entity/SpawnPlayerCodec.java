package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class SpawnPlayerCodec implements Codec<SpawnPlayerMessage> {

    @Override
    public SpawnPlayerMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        UUID uuid = GlowBufUtils.readUuid(buf);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        List<Entry> list = GlowBufUtils.readMetadata(buf);
        return new SpawnPlayerMessage(id, uuid, x, y, z, rotation, pitch, list);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeByte(message.getRotation());
        buf.writeByte(message.getPitch());
        GlowBufUtils.writeMetadata(buf, message.getMetadata());
        return buf;
    }
}
