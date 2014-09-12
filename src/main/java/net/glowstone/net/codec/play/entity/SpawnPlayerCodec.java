package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap;
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
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int rotation = buf.readByte();
        int pitch = buf.readByte();
        int item = buf.readShort();
        List<MetadataMap.Entry> list = GlowBufUtils.readMetadata(buf);
        return new SpawnPlayerMessage(id, uuid, x, y, z, rotation, pitch, item, list);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPlayerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUuid());
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
